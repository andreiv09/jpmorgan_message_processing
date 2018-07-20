package org.av.message;

import org.av.util.Money;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PriceAdjustmentMessageTest {
    private static Stream<Arguments> addSubs() {
        return Stream.of(
            Arguments.of(null, null, false, false),
            Arguments.of("", null, false, false),
            Arguments.of("apple", null, false, false),
            Arguments.of("apple", new Money("-10p"), true, true)
        );
    }

    private static Stream<Arguments> multiply() {
        return Stream.of(
            Arguments.of(null, null, false),
            Arguments.of("", null, false),
            Arguments.of("apple", null, false),
            Arguments.of("apple", BigDecimal.ONE, true)
        );
    }

    @ParameterizedTest
    @MethodSource("addSubs")
    void testAddSub(String product, Money price, boolean add, boolean expected) {
        PriceAdjustmentMessage pam = new PriceAdjustmentMessage(product, price, add);
        assertEquals(expected, pam.isValid());
        assertTrue(pam.isAddSub());
        assertFalse(pam.isMultiply());
        assertThrows(IllegalStateException.class, pam::getMultiplier);
        assertDoesNotThrow(pam::getPrice);
        assertEquals(product, pam.getProduct());
        assertEquals(price, pam.getPrice());
        assertEquals(add, pam.isPriceIncrease());
    }

    @ParameterizedTest
    @MethodSource("multiply")
    void testMultiply(String product, BigDecimal multiplier, boolean expected) {
        PriceAdjustmentMessage pam = new PriceAdjustmentMessage(product, multiplier);
        assertEquals(expected, pam.isValid());
        assertTrue(pam.isMultiply());
        assertFalse(pam.isAddSub());
        assertThrows(IllegalStateException.class, pam::getPrice);
        assertDoesNotThrow(pam::getMultiplier);
        assertEquals(product, pam.getProduct());
        assertEquals(multiplier, pam.getMultiplier());
        assertThrows(IllegalStateException.class, pam::isPriceIncrease);
    }
}