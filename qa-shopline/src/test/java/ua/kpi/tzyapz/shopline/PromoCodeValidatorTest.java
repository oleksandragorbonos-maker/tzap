package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        PromoCode promo = new PromoCode("SAVE10", PromoCode.Type.PERCENT,
                BigDecimal.valueOf(percent), EXPIRES_FUTURE, false, false);
        BigDecimal result = validator.apply(promo, TODAY, new BigDecimal(subtotalStr));
        assertEquals(new BigDecimal(expectedStr), result);
    }

    @Test
    @DisplayName("BL-80: FIXED — повертає фіксовану суму коду")
    void givenValidFixedPromo_whenApply_returnsFixedAmount() {
        PromoCode promo = new PromoCode("FIXED50", PromoCode.Type.FIXED,
                new BigDecimal("50.00"), EXPIRES_FUTURE, false, false);
        BigDecimal result = validator.apply(promo, TODAY, new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    @DisplayName("BL-80: прострочений промокод — apply кидає IllegalArgumentException")
    void givenExpiredPromo_whenApply_throwsIllegalArgument() {
        PromoCode promo = new PromoCode("SAVE10", PromoCode.Type.PERCENT,
                BigDecimal.valueOf(10), LocalDate.of(2026, 11, 8), false, false);
        assertThrows(IllegalArgumentException.class,
                () -> validator.apply(promo, TODAY, new BigDecimal("1000.00")));
    }

    // Дефект #80: validate використовує today.isBefore(expiresOn) (ексклюзивна межа),
    // тому промокод з expiresOn == today відхиляється, а SRS вимагає «день закінчення включно»
    @Test
    @DisplayName("BL-80: промокод діє до today включно — apply повертає знижку")
    void givenPromoExpiresOnToday_whenApply_returnsDiscount() {
        PromoCode promo = new PromoCode("SAVE10", PromoCode.Type.PERCENT,
                BigDecimal.valueOf(10), TODAY, false, false);
        BigDecimal result = validator.apply(promo, TODAY, new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("100.00"), result);
    }

    // Дефект #81: validate і apply не перевіряють singleUse && used,
    // тому використаний одноразовий промокод не відхиляється (порушення BL-80)
    @Test
    @DisplayName("BL-80: використаний одноразовий промокод — apply кидає IllegalArgumentException")
    void givenUsedSingleUsePromo_whenApply_throwsIllegalArgument() {
        PromoCode promo = new PromoCode("ONCE01", PromoCode.Type.PERCENT,
                BigDecimal.valueOf(10), EXPIRES_FUTURE, true, true);
        assertThrows(IllegalArgumentException.class,
                () -> validator.apply(promo, TODAY, new BigDecimal("1000.00")));
    }
}
