package org.av.message;

import org.av.util.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageValidatorsTest {
    
    private static Stream<String> invalidProducts() {
        return Stream.of(null, "");
    }
    
    private static Stream<Money> invalidPrices() {
        return Stream.of(
            null,
            new Money("-1"),
            new Money("-0.01"));
    }
    
    private static Stream<Money> validPrices() {
        return Stream.of(
            new Money("0"),
            new Money("1"),
            new Money("100"),
            new Money("100.25"),
            new Money("0p"),
            new Money("42p"));
    }
    
    private static Stream<Integer> invalidQuantities() {
        return Stream.of(null, -1, -1000);
    }
    
    @ParameterizedTest
    @MethodSource("invalidProducts")
    void isInvalidProduct(String prod) {
        assertFalse(MessageValidators.isValidProduct(prod));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"apple", "a", "-chars@"})
    void isValidProduct(String prod) {
        assertTrue(MessageValidators.isValidProduct(prod));
    }
    
    @ParameterizedTest
    @MethodSource("invalidPrices")
    void isInvalidPrice(Money price) {
        assertFalse(MessageValidators.isValidPrice(price));
    }
    
    @ParameterizedTest
    @MethodSource("validPrices")
    void isValidPrice(Money price) {
        assertTrue(MessageValidators.isValidPrice(price));
    }
    
    @ParameterizedTest
    @MethodSource("invalidQuantities")
    void isInvalidQuantity(Integer qty) {
        assertFalse(MessageValidators.isValidQuantity(qty));
    }
    
    @Test
    void isInvalidMultiplier() {
        assertFalse(MessageValidators.isValidMultiplier(null));
    }
    
    @Test
    void isValidMultiplier() {
        assertTrue(MessageValidators.isValidMultiplier(BigDecimal.ZERO));
    }
    
    @Test
    void isValidPriceAdjustment() {
        assertTrue(MessageValidators.isValidPriceAdjustment(new Money("0")));
    }
    
    @Test
    void isInvalidPriceAdjustment() {
        assertFalse(MessageValidators.isValidPriceAdjustment(null));
    }
}