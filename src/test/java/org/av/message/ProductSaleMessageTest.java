package org.av.message;

import org.av.util.Money;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductSaleMessageTest {
    private static Stream<Arguments> productSales() {
        return Stream.of(
            Arguments.of(null, null, null, false),
            Arguments.of("", null, null, false),
            Arguments.of("apple", null, null, false),
            Arguments.of("apple", new Money("1"), null, false),
            Arguments.of("apple", new Money("1"), 1, true)
        );
    }
    
    @ParameterizedTest
    @MethodSource("productSales")
    void isValid(String product, Money price, Integer qty, Boolean expected) {
        ProductSaleMessage psm = new ProductSaleMessage(product, price, qty);
        assertEquals(expected, psm.isValid());
        assertEquals(product, psm.getProduct());
        assertEquals(price, psm.getPrice());
        assertEquals(qty, psm.getQty());
    }
}