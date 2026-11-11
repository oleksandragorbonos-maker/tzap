package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Юніт-тести BL-70: PromoCodeValidator.validate (SRS 1.1).
 * Очікувані значення взято з SRS і таблиці docs/sprint4/equivalence-boundary.md, а не з коду модуля.
 */
@DisplayName("ШопЛайн · PromoCodeValidator.validate (BL-70)")
class PromoCodeValidationTest {

    // Фіксована дата, щоб результат не залежав від дня запуску (LocalDate.now() для меж дати не підходить).
    private static final LocalDate TODAY = LocalDate.of(2026, 3, 15);
    private static final LocalDate FAR_FUTURE = TODAY.plusDays(30);
    private static final String VALID_CODE = "PROMO2026";

    private PromoCodeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PromoCodeValidator();
    }

    /** Багаторазовий промокод типу PERCENT із заданим кодом, відсотком і датою закінчення. */
    private static PromoCode percentPromo(String code, String percent, LocalDate expiresOn) {
        return new PromoCode(code, PromoCode.Type.PERCENT, new BigDecimal(percent), expiresOn, false, false);
    }

    private static PromoCode fixedPromo(String amount) {
        return new PromoCode(VALID_CODE, PromoCode.Type.FIXED, new BigDecimal(amount), FAR_FUTURE, false, false);
    }

    private static PromoCode singleUsePromo(boolean used) {
        return new PromoCode(VALID_CODE, PromoCode.Type.PERCENT, new BigDecimal("10"), FAR_FUTURE, true, used);
    }

    private PromoCodeValidator.ValidationResult validate(PromoCode promo, String subtotal) {
        return validator.validate(promo, TODAY, new BigDecimal(subtotal));
    }

    // ---- довжина коду 6..10 ----

    @ParameterizedTest(name = "довжина {0}")
    @ValueSource(ints = {6, 7, 9, 10})
    @DisplayName("BL-70: код довжиною від 6 до 10 символів придатний")
    void givenCodeLengthWithin6To10_whenValidate_isValid(int length) {
        PromoCode promo = percentPromo("A".repeat(length), "10", FAR_FUTURE);

        PromoCodeValidator.ValidationResult result = validate(promo, "1000.00");

        assertTrue(result.valid(), "Код довжиною " + length + " має бути придатним: " + result.reason());
    }

    @ParameterizedTest(name = "довжина {0}")
    @ValueSource(ints = {0, 5, 11})
    @DisplayName("BL-70: код довжиною менше 6 або більше 10 символів не придатний, причина вказана")
    void givenCodeLengthOutOfRange_whenValidate_isInvalid(int length) {
        PromoCode promo = percentPromo("A".repeat(length), "10", FAR_FUTURE);

        PromoCodeValidator.ValidationResult result = validate(promo, "1000.00");

        assertAll(
                () -> assertFalse(result.valid(), "Код довжиною " + length + " не має бути придатним"),
                () -> assertFalse(result.reason().isBlank(), "Для непридатного коду причина не може бути порожньою")
        );
    }

    // ---- дата закінчення: день закінчення включно ----

    // Дефект #80: validate використовує today.isBefore(expiresOn) — день закінчення відхиляється.
    @ParameterizedTest(name = "expiresOn = today {0} дн. → придатний: {1}")
    @CsvSource({
            "-1, false",
            "0, true",
            "1, true"
    })
    @DisplayName("BL-70: код діє до дня закінчення включно (today−1 — ні, today — так, today+1 — так)")
    void givenExpiryAroundToday_whenValidate_expiryDayIsInclusive(int daysFromToday, boolean expectedValid) {
        PromoCode promo = percentPromo(VALID_CODE, "10", TODAY.plusDays(daysFromToday));

        PromoCodeValidator.ValidationResult result = validate(promo, "1000.00");

        assertEquals(expectedValid, result.valid(), "Результат для expiresOn = today " + daysFromToday + " дн.: " + result.reason());
    }

    // ---- одноразовий код ----

    // Дефект #81: validate не перевіряє ознаку used для одноразового коду.
    @ParameterizedTest(name = "одноразовий, used = {0} → придатний: {1}")
    @CsvSource({
            "true, false",
            "false, true"
    })
    @DisplayName("BL-70: використаний одноразовий код не придатний, невикористаний — придатний")
    void givenSingleUseCode_whenValidate_usedCodeIsRejected(boolean used, boolean expectedValid) {
        PromoCode promo = singleUsePromo(used);

        PromoCodeValidator.ValidationResult result = validate(promo, "1000.00");

        assertEquals(expectedValid, result.valid(), "Одноразовий код, used = " + used + ": " + result.reason());
    }

    // ---- PERCENT 1..50 ----

    @ParameterizedTest(name = "відсоток {0} → придатний: {1}")
    @CsvSource({
            "0, false",
            "0.99, false",
            "1, true",
            "50, true",
            "50.01, false",
            "51, false"
    })
    @DisplayName("BL-70: відсоток знижки допустимий лише в межах 1..50 включно")
    void givenPercentValueAroundBounds_whenValidate_acceptsOnly1To50(String percent, boolean expectedValid) {
        PromoCode promo = percentPromo(VALID_CODE, percent, FAR_FUTURE);

        PromoCodeValidator.ValidationResult result = validate(promo, "1000.00");

        assertEquals(expectedValid, result.valid(), "Відсоток " + percent + ": " + result.reason());
    }

    // ---- FIXED: більше 0 і не більше підсумку (підсумок < 2000, знижки за обсяг немає) ----

    @ParameterizedTest(name = "сума {0} при підсумку 1000.00 → придатний: {1}")
    @CsvSource({
            "-5.00, false",
            "0.00, false",
            "0.01, true",
            "1000.00, true",
            "1000.01, false"
    })
    @DisplayName("BL-70: фіксована сума має бути більшою за 0 і не більшою за підсумок")
    void givenFixedValueAroundBounds_whenValidate_acceptsPositiveUpToSubtotal(String amount, boolean expectedValid) {
        PromoCode promo = fixedPromo(amount);

        PromoCodeValidator.ValidationResult result = validate(promo, "1000.00");

        assertEquals(expectedValid, result.valid(), "Фіксована сума " + amount + ": " + result.reason());
    }

    // Дефект #89: SRS вимагає ліміт «підсумок мінус знижка за обсяг (BL-50)»,
    // а код порівнює лише з підсумком.
    @ParameterizedTest(name = "підсумок {0}, сума {1} → придатний: {2}")
    @CsvSource({
            "1999.99, 1999.99, true",
            "1999.99, 2000.00, false",
            "2000.00, 1900.00, true",
            "2000.00, 1900.01, false",
            "4999.99, 4749.99, true",
            "4999.99, 4750.00, false",
            "5000.00, 4500.00, true",
            "5000.00, 4500.01, false",
            "10000.00, 8500.00, true",
            "10000.00, 8500.01, false"
    })
    @DisplayName("BL-70: фіксована сума не перевищує підсумок мінус знижка за обсяг (BL-50)")
    void givenFixedValueAndVolumeDiscount_whenValidate_limitIsSubtotalMinusDiscount(
            String subtotal, String amount, boolean expectedValid) {
        PromoCode promo = fixedPromo(amount);

        PromoCodeValidator.ValidationResult result = validate(promo, subtotal);

        assertEquals(expectedValid, result.valid(),
                "Підсумок " + subtotal + ", фіксована сума " + amount + ": " + result.reason());
    }

    // ---- порядок причин відмови ----

    // Формат причини (код INVALID_* чи текст) — питання Q1, тому перевіряється не текст, а правило SRS:
    // при кількох порушеннях повертається причина ПЕРШОГО з них (довжина → дата → одноразовість → відсоток).
    // Рядок про одноразовість червоний через дефект #81.
    @Test
    @DisplayName("BL-70: при кількох порушеннях повертається причина першого за порядком SRS")
    void givenSeveralViolations_whenValidate_returnsFirstReasonInSrsOrder() {
        // arrange: коди з одним порушенням — еталонні причини
        String lengthReason = validate(percentPromo("ABC", "10", FAR_FUTURE), "1000.00").reason();
        String expiredReason = validate(percentPromo(VALID_CODE, "10", TODAY.minusDays(1)), "1000.00").reason();
        String usedReason = validate(singleUsePromo(true), "1000.00").reason();

        // act: коди з кількома порушеннями
        String lengthAndExpired = validate(percentPromo("ABC", "10", TODAY.minusDays(1)), "1000.00").reason();
        PromoCode expiredAndUsed = new PromoCode(VALID_CODE, PromoCode.Type.PERCENT, new BigDecimal("10"),
                TODAY.minusDays(1), true, true);
        String expiredAndUsedReason = validate(expiredAndUsed, "1000.00").reason();
        PromoCode usedAndBadPercent = new PromoCode(VALID_CODE, PromoCode.Type.PERCENT, new BigDecimal("77"),
                FAR_FUTURE, true, true);
        String usedAndBadPercentReason = validate(usedAndBadPercent, "1000.00").reason();

        // assert
        assertAll(
                () -> assertNotEquals(lengthReason, expiredReason, "Причини довжини і дати мають відрізнятися"),
                () -> assertNotEquals(expiredReason, usedReason, "Причини дати і одноразовості мають відрізнятися"),
                () -> assertEquals(lengthReason, lengthAndExpired, "Довжина і дата: має повертатися причина довжини"),
                () -> assertEquals(expiredReason, expiredAndUsedReason, "Дата і одноразовість: має повертатися причина дати"),
                () -> assertEquals(usedReason, usedAndBadPercentReason, "Одноразовість і відсоток: має повертатися причина одноразовості")
        );
    }

    @Test
    @DisplayName("BL-70: для придатного коду причина відмови порожня")
    void givenValidCode_whenValidate_reasonIsEmpty() {
        PromoCode promo = percentPromo(VALID_CODE, "10", FAR_FUTURE);

        PromoCodeValidator.ValidationResult result = validate(promo, "1000.00");

        assertAll(
                () -> assertTrue(result.valid(), "Код має бути придатним: " + result.reason()),
                () -> assertTrue(result.reason().isEmpty(), "Причина для придатного коду має бути порожньою")
        );
    }
}
