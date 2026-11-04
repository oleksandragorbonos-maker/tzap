package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Юніт-тести BL-20: повторне додавання позиції з тим самим sku (SRS 1.1). */
@DisplayName("ШопЛайн · Cart: повторне додавання (BL-20)")
class CartRepeatedAddTest {

    private static final String SKU = "HAM-010";
    private static final String NAME = "Молоток слюсарний";
    private static final BigDecimal PRICE = new BigDecimal("350.00");

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    // Дефект #__: повторний addItem замінює кількість замість збільшення (items.put)
    @Test
    @DisplayName("BL-20: повторне додавання того самого sku збільшує кількість, а не створює новий рядок")
    void givenSameSku_whenAddItemTwice_quantityIsSummedInSingleLine() {
        // arrange
        cart.addItem(SKU, NAME, PRICE, 2);
        // act
        cart.addItem(SKU, NAME, PRICE, 3);
        // assert
        assertAll(
                () -> assertEquals(1, cart.items().size(), "кількість рядків"),
                () -> assertEquals(5, cart.qtyOf(SKU), "кількість у рядку"),
                () -> assertEquals(5, cart.itemCount(), "itemCount"),
                () -> assertEquals(new BigDecimal("1750.00"), cart.subtotal(), "subtotal")
        );
    }

    // Дефект #__: той самий дефект, перевірка межі сумарної кількості 99 включно
    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({"1, 1, 2", "50, 49, 99", "98, 1, 99", "1, 98, 99"})
    @DisplayName("BL-20: сумарна кількість до 99 включно приймається")
    void givenTotalUpTo99_whenAddItemTwice_quantitiesAreSummed(int first, int second, int expectedTotal) {
        cart.addItem(SKU, NAME, PRICE, first);

        cart.addItem(SKU, NAME, PRICE, second);

        assertEquals(expectedTotal, cart.qtyOf(SKU));
    }

    // Дефект #__: після повторного додавання немає перевірки ліміту 99 (кількість просто замінюється)
    @ParameterizedTest(name = "{0} + {1} > 99")
    @CsvSource({"99, 1", "98, 2", "50, 50", "1, 99"})
    @DisplayName("BL-20: сумарна кількість понад 99 відхиляється, кошик не змінюється")
    void givenTotalAbove99_whenAddItemTwice_throwsIllegalArgumentAndCartUnchanged(int first, int second) {
        cart.addItem(SKU, NAME, PRICE, first);

        assertThrows(IllegalArgumentException.class, () -> cart.addItem(SKU, NAME, PRICE, second));

        assertAll(
                () -> assertEquals(first, cart.qtyOf(SKU), "кількість не змінилась"),
                () -> assertEquals(1, cart.items().size(), "кількість рядків")
        );
    }

    @Test
    @DisplayName("BL-20: різні sku не об'єднуються — окремі рядки")
    void givenDifferentSkus_whenAddItem_separateLinesAreCreated() {
        cart.addItem(SKU, NAME, PRICE, 2);
        cart.addItem("PLR-020", "Плоскогубці комбіновані", new BigDecimal("199.99"), 1);

        assertAll(
                () -> assertEquals(2, cart.items().size(), "кількість рядків"),
                () -> assertEquals(2, cart.qtyOf(SKU)),
                () -> assertEquals(1, cart.qtyOf("PLR-020"))
        );
    }
}
