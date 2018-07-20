package org.av.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {
    private static Stream<String> badMoney() {
        return Stream.of(
            "abc",
            "",
            null,
            "123a",
            "a123",
            " 356",
            "123.233.4",
            "12.3p"
        );
    }

    private static Stream<String> goodMoney() {
        return Stream.of(
            "0",
            "1",
            "3.14",
            "42p",
            "0p"
        );
    }

    @ParameterizedTest
    @MethodSource("badMoney")
    void invalidMoneys(String money) {
        assertThrows(IllegalArgumentException.class, () -> new Money(money));
    }

    @ParameterizedTest
    @MethodSource("goodMoney")
    void validMoneys(String money) {
        assertDoesNotThrow(() -> new Money(money));
    }

    @Test
    void add() {
        assertEquals(new Money("3.5"), new Money("1.5").add(new Money("2")));
        assertEquals(new Money("1.05"), new Money("1.0").add(new Money("5p")));
    }


    @Test
    void subtract() {
        assertEquals(new Money("1.5"), new Money("3").subtract(new Money("1.5")));
        assertEquals(new Money("1.0"), new Money("1.05").subtract(new Money("5p")));
    }

    @Test
    void multiply() {
        assertEquals(new Money("3.0"), new Money("1.5").multiply(new BigDecimal("2")));
    }


    @Test
    void equals() {
        assertTrue((new Money("3.14")).equals(new Money("3.14")));
    }

    @Test
    void compareTo() {
        Money zero = new Money("0");
        Money one = new Money("1");
        assertTrue(zero.compareTo(one) < 0);
        assertTrue(zero.compareTo(zero) == 0);
        assertTrue(one.compareTo(one) == 0);
        assertTrue(one.compareTo(zero) > 0);
    }
}