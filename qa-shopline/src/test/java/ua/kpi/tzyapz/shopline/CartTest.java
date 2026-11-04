package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ШопЛайн · Cart (BL-10, BL-40)")
class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @ParameterizedTest(name = "qty = {0}")
    @ValueSource(ints = {1, 2, 98, 99})
    @DisplayName("BL-10: кількість від 1 до 99 приймається")
    void givenQtyWithinRange_whenAddItem_itemIsAdded(int qty) {
        cart.addItem("HAM-010", "Молоток слюсарний", new BigDecimal("350.00"), qty);

        assertEquals(qty, cart.qtyOf("HAM-010"));
    }

    @ParameterizedTest(name = "qty = {0}")
    @ValueSource(ints = {0, -1, 100, 101})
    @DisplayName("BL-10: кількість поза межами 1..99 відхиляється")
    void givenQtyOutOfRange_whenAddItem_throwsIllegalArgument(int qty) {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("HAM-010", "Молоток слюсарний", new BigDecimal("350.00"), qty));
    }

    @ParameterizedTest(name = "ціна = {0}")
    @ValueSource(strings = {"0.00", "-0.01", "-100.00"})
    @DisplayName("BL-10: нульова або від'ємна ціна відхиляється")
    void givenNonPositivePrice_whenAddItem_throwsIllegalArgument(String price) {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("HAM-010", "Молоток слюсарний", new BigDecimal(price), 1));
    }

    @ParameterizedTest(name = "ціна = {0}")
    @ValueSource(strings = {"0.004", "0.001"})
    @DisplayName("BL-10: ціна, що після округлення HALF_UP дорівнює 0.00, відхиляється")
    void givenPriceRoundingToZero_whenAddItem_throwsIllegalArgument(String price) {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("HAM-010", "Молоток слюсарний", new BigDecimal(price), 1));
    }

    @ParameterizedTest(name = "sku = \"{0}\"")
    @EmptySource
    @DisplayName("BL-10: порожній sku відхиляється")
    void givenEmptySku_whenAddItem_throwsIllegalArgument(String sku) {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem(sku, "Молоток слюсарний", new BigDecimal("350.00"), 1));
    }

    @ParameterizedTest(name = "name = \"{0}\"")
    @EmptySource
    @DisplayName("BL-10: порожній name відхиляється")
    void givenEmptyName_whenAddItem_throwsIllegalArgument(String name) {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("HAM-010", name, new BigDecimal("350.00"), 1));
    }

    @Test
    @DisplayName("BL-10: після відхиленого addItem вміст кошика не змінюється")
    void givenRejectedAdd_whenCheckingCart_cartIsUnchanged() {
        cart.addItem("HAM-010", "Молоток слюсарний", new BigDecimal("350.00"), 2);

        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("PLR-020", "Плоскогубці комбіновані", new BigDecimal("199.99"), 0));

        assertAll(
                () -> assertEquals(2, cart.itemCount()),
                () -> assertEquals(new BigDecimal("700.00"), cart.subtotal())
        );
    }

    @ParameterizedTest(name = "ціна {0} → {1}")
    @CsvSource({"10.004, 10.00", "10.005, 10.01", "19.994, 19.99", "19.995, 20.00"})
    @DisplayName("BL-10: ціна зберігається з округленням до копійок за HALF_UP")
    void givenPriceWithThreeDecimals_whenAddItem_priceIsRoundedHalfUp(String price, String expected) {
        cart.addItem("HAM-010", "Молоток слюсарний", new BigDecimal(price), 1);

        assertEquals(new BigDecimal(expected), cart.subtotal());
    }
}
