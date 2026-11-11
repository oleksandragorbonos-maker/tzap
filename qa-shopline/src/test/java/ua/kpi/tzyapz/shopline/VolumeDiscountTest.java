package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Юніт-тести BL-50: знижка за обсяг DiscountCalculator.volumeDiscountPercent / volumeDiscount (SRS 1.1).
 * Очікувані значення взято з SRS і таблиці docs/sprint4/equivalence-boundary.md, а не з коду модуля.
 */
@DisplayName("ШопЛайн · DiscountCalculator.volumeDiscount (BL-50)")
class VolumeDiscountTest {

    private DiscountCalculator calc;

    @BeforeEach
    void setUp() {
        calc = new DiscountCalculator();
    }

    // Кандидат у дефекти D-3: поріг 5000.00 дає 5 % замість 10 % (у коді «> 5000» замість «≥ 5000»).
    // Номер звіту — після заведення Issue (мітка module).
    @ParameterizedTest(name = "підсумок {0} → {1} %")
    @CsvSource({
            "0.00, 0",
            "1999.99, 0",
            "2000.00, 5",
            "2000.01, 5",
            "4999.99, 5",
            "5000.00, 10",
            "5000.01, 10",
            "9999.99, 10",
            "10000.00, 15",
            "10000.01, 15"
    })
    @DisplayName("BL-50: відсоток знижки за порогами 2000 / 5000 / 10000 (значення з обох боків порога)")
    void givenSubtotalAtTierBoundaries_whenVolumeDiscountPercent_returnsTierPercent(String subtotal, int expectedPercent) {
        // arrange
        BigDecimal amount = new BigDecimal(subtotal);
        // act
        int percent = calc.volumeDiscountPercent(amount);
        // assert
        assertEquals(expectedPercent, percent);
    }

    // Кандидати у дефекти D-3 (поріг 5000.00) і D-4 (округлення DOWN замість HALF_UP: 4999.99 → 249.99).
    @ParameterizedTest(name = "підсумок {0} → знижка {1}")
    @CsvSource({
            "0.00, 0.00",
            "1999.99, 0.00",
            "2000.00, 100.00",
            "2000.01, 100.00",
            "4999.99, 250.00",
            "5000.00, 500.00",
            "5000.01, 500.00",
            "9999.99, 1000.00",
            "10000.00, 1500.00",
            "10000.01, 1500.00",
            "15000.00, 2250.00"
    })
    @DisplayName("BL-50: сума знижки на межах порогів (шкала 2)")
    void givenSubtotalAtTierBoundaries_whenVolumeDiscount_returnsDiscountAmount(String subtotal, String expectedDiscount) {
        BigDecimal amount = new BigDecimal(subtotal);

        BigDecimal discount = calc.volumeDiscount(amount);

        assertEquals(new BigDecimal(expectedDiscount), discount);
    }

    // Кандидат у дефекти D-4: у коді RoundingMode.DOWN замість HALF_UP.
    // 2333.33 × 5 % = 116.6665 → 116.67; 2000.10 × 5 % = 100.005 (рівно «половина») → 100.01.
    @ParameterizedTest(name = "підсумок {0} → знижка {1}")
    @CsvSource({
            "2333.33, 116.67",
            "2000.10, 100.01"
    })
    @DisplayName("BL-50: знижка округлюється до копійок за правилом HALF_UP")
    void givenAmountWithHalfCent_whenVolumeDiscount_roundsHalfUp(String subtotal, String expectedDiscount) {
        BigDecimal amount = new BigDecimal(subtotal);

        BigDecimal discount = calc.volumeDiscount(amount);

        assertEquals(new BigDecimal(expectedDiscount), discount);
    }

    @ParameterizedTest(name = "підсумок {0} → знижка 0.00")
    @ValueSource(strings = {"0.01", "500.00", "1000.00"})
    @DisplayName("BL-50: для підсумку менше 2000 знижка дорівнює 0.00")
    void givenSubtotalBelow2000_whenVolumeDiscount_returnsZero(String subtotal) {
        BigDecimal discount = calc.volumeDiscount(new BigDecimal(subtotal));

        assertEquals(new BigDecimal("0.00"), discount);
    }

    @ParameterizedTest(name = "підсумок {0} → знижка {1}")
    @CsvSource({
            "7000.00, 700.00",
            "10000.00, 1500.00",
            "20000.00, 3000.00"
    })
    @DisplayName("BL-50: застосовується лише найвищий досягнутий рівень, рівні не сумуються")
    void givenSubtotalInHigherTier_whenVolumeDiscount_appliesOnlyHighestTier(String subtotal, String expectedDiscount) {
        BigDecimal discount = calc.volumeDiscount(new BigDecimal(subtotal));

        assertEquals(new BigDecimal(expectedDiscount), discount);
    }

    @ParameterizedTest(name = "підсумок {0}")
    @ValueSource(strings = {"-0.01", "-1.00", "-2000.00"})
    @DisplayName("BL-50: від'ємний підсумок — volumeDiscountPercent кидає IllegalArgumentException")
    void givenNegativeSubtotal_whenVolumeDiscountPercent_throwsIllegalArgument(String subtotal) {
        BigDecimal amount = new BigDecimal(subtotal);

        assertThrows(IllegalArgumentException.class, () -> calc.volumeDiscountPercent(amount));
    }

    @ParameterizedTest(name = "підсумок {0}")
    @ValueSource(strings = {"-0.01", "-1.00", "-2000.00"})
    @DisplayName("BL-50: від'ємний підсумок — volumeDiscount кидає IllegalArgumentException")
    void givenNegativeSubtotal_whenVolumeDiscount_throwsIllegalArgument(String subtotal) {
        BigDecimal amount = new BigDecimal(subtotal);

        assertThrows(IllegalArgumentException.class, () -> calc.volumeDiscount(amount));
    }
}
