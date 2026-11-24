package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Юніт-тести BL-60: вартість доставки DiscountCalculator.shippingFee (SRS 1.1). */
@DisplayName("ШопЛайн · DiscountCalculator.shippingFee (BL-60)")
class ShippingFeeTest {

    private DiscountCalculator calc;

    @BeforeEach
    void setUp() {
        calc = new DiscountCalculator();
    }

    @ParameterizedTest(name = "після знижки {0} → доставка {1}")
    @CsvSource({
            "0.00, 120.00",
            "0.01, 120.00",
            "1499.99, 120.00",
            "1500.00, 0.00",
            "1500.01, 0.00",
            "10000.00, 0.00"
    })
    @DisplayName("BL-60: доставка 0.00 від 1500 грн (включно), інакше 120.00")
    void givenSubtotalAfterDiscount_whenShippingFee_returnsFeeByThreshold(String subtotalAfterDiscount, String expectedFee) {
        assertEquals(new BigDecimal(expectedFee), calc.shippingFee(new BigDecimal(subtotalAfterDiscount)));
    }

    @ParameterizedTest(name = "сума = {0}")
    @ValueSource(strings = {"-0.01", "-1.00", "-1500.00"})
    @DisplayName("BL-60: від'ємна сума — IllegalArgumentException")
    void givenNegativeAmount_whenShippingFee_throwsIllegalArgument(String amount) {
        assertThrows(IllegalArgumentException.class, () -> calc.shippingFee(new BigDecimal(amount)));
    }

    @Test
    @DisplayName("BL-60: поріг 1500 застосовується до суми ПІСЛЯ знижок, а не до початкового підсумку")
    void givenSubtotalAbove1500_whenDiscountDropsItBelow_shippingIsCharged() {
        // arrange: підсумок 1600.00, знижка за промокодом 150.00 → 1450.00 після знижки
        BigDecimal afterDiscount = new BigDecimal("1600.00").subtract(new BigDecimal("150.00"));
        // act
        BigDecimal fee = calc.shippingFee(afterDiscount);
        // assert
        assertEquals(new BigDecimal("120.00"), fee);
    }

    @Test
    @DisplayName("BL-60: сума після знижки за обсяг (BL-50) 1900.00 ≥ 1500 — доставка безкоштовна")
    void givenVolumeDiscountApplied_whenShippingFee_isFree() {
        BigDecimal subtotal = new BigDecimal("2000.00");
        BigDecimal afterDiscount = subtotal.subtract(calc.volumeDiscount(subtotal));

        assertEquals(new BigDecimal("0.00"), calc.shippingFee(afterDiscount));
    }
}
