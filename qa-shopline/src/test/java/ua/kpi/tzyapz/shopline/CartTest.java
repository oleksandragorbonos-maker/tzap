package ua.kpi.tzyapz.shopline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

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
}
