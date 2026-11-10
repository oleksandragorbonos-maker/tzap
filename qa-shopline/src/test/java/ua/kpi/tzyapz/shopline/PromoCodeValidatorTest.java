package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Юніт-тести BL-80: PromoCodeValidator.apply (SRS 1.1). */
@DisplayName("ШопЛайн · PromoCodeValidator.apply (BL-80)")
class PromoCodeValidatorTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 9);
    private static final LocalDate EXPIRES_FUTURE = LocalDate.of(2026, 11, 20);

    private PromoCodeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PromoCodeValidator();
    }

    /** Багаторазовий (не одноразовий, не використаний) промокод. */
    private static PromoCode reusablePromo(String code, PromoCode.Type type, BigDecimal value, LocalDate expiresOn) {
        return new PromoCode(code, type, value, expiresOn, /* singleUse */ false, /* used */ false);
    }

    /** Одноразовий промокод, який уже використано. */
    private static PromoCode usedSingleUsePromo(String code, PromoCode.Type type, BigDecimal value, LocalDate expiresOn) {
        return new PromoCode(code, type, value, expiresOn, /* singleUse */ true, /* used */ true);
    }

    @ParameterizedTest(name = "subtotal={0}, percent={1} → {2}")
    @CsvSource({
            "1000.00, 10, 100.00",
            "1000.00,  1,  10.00",
            "1000.00, 50, 500.00",
            "   3.35, 10,   0.34",
            "   3.34, 10,   0.33"
    })
    @DisplayName("BL-80: PERCENT — subtotal × відсоток / 100, округлення HALF_UP")
    void givenPercentPromo_whenApply_returnsRoundedPercent(String subtotalStr, int percent, String expectedStr) {
        PromoCode promo = reusablePromo("SAVE10", PromoCode.Type.PERCENT,
                BigDecimal.valueOf(percent), EXPIRES_FUTURE);
        BigDecimal result = validator.apply(promo, TODAY, new BigDecimal(subtotalStr));
        assertEquals(new BigDecimal(expectedStr), result);
    }

    @ParameterizedTest(name = "subtotal={0} → 50.00")
    @CsvSource({
            "1000.00",
            " 200.00",
            "  50.00"
    })
    @DisplayName("BL-80: FIXED — повертає фіксовану суму коду незалежно від subtotal")
    void givenValidFixedPromo_whenApply_returnsFixedAmount(String subtotalStr) {
        PromoCode promo = reusablePromo("FIXED50", PromoCode.Type.FIXED,
                new BigDecimal("50.00"), EXPIRES_FUTURE);
        BigDecimal result = validator.apply(promo, TODAY, new BigDecimal(subtotalStr));
        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    @DisplayName("BL-80: прострочений промокод — apply кидає IllegalArgumentException із причиною відмови")
    void givenExpiredPromo_whenApply_throwsIllegalArgument() {
        PromoCode promo = reusablePromo("SAVE10", PromoCode.Type.PERCENT,
                BigDecimal.valueOf(10), LocalDate.of(2026, 11, 8));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.apply(promo, TODAY, new BigDecimal("1000.00")));
        assertFalse(ex.getMessage().isBlank());
    }

    // Дефект #80: validate використовує today.isBefore(expiresOn) (ексклюзивна межа),
    // тому промокод з expiresOn == today відхиляється, а SRS вимагає «день закінчення включно»
    @Test
    @DisplayName("BL-80: промокод діє до today включно — apply повертає знижку")
    void givenPromoExpiresOnToday_whenApply_returnsDiscount() {
        PromoCode promo = reusablePromo("SAVE10", PromoCode.Type.PERCENT,
                BigDecimal.valueOf(10), TODAY);
        BigDecimal result = validator.apply(promo, TODAY, new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("100.00"), result);
    }

    // Дефект #81: validate і apply не перевіряють singleUse && used,
    // тому використаний одноразовий промокод не відхиляється (порушення BL-80)
    @Test
    @DisplayName("BL-80: використаний одноразовий промокод — apply кидає IllegalArgumentException із причиною відмови")
    void givenUsedSingleUsePromo_whenApply_throwsIllegalArgument() {
        PromoCode promo = usedSingleUsePromo("ONCE01", PromoCode.Type.PERCENT,
                BigDecimal.valueOf(10), EXPIRES_FUTURE);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.apply(promo, TODAY, new BigDecimal("1000.00")));
        assertFalse(ex.getMessage().isBlank());
    }
}
