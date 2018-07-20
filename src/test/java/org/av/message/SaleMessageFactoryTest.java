package org.av.message;

import org.av.util.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SaleMessageFactoryTest {
    private static SaleMessageFactory smf;
    
    @BeforeAll
    static void init() {
        smf = new SaleMessageFactory();
    }
    
    private static Stream<String> invalidMesgs() {
        return Stream.of(
            null,
            "",
            " at 10p",
            "apple at",
            "apple at p",
            "apple at -10p",
            "10 apple",
            "2 sales of apple at p",
            "10 sales of apple",
            "add 10",
            "add apple",
            "multiply 10p apple"
        );
    }
    
    private static Stream<Arguments> validSingleSales() {
        return Stream.of(
            Arguments.of("apple at 10p", "apple", new Money("10p")),
            Arguments.of("orange soda at 1", "orange soda", new Money("1")),
            Arguments.of("cherry tomatoes at 2.5", "cherry tomatoes", new Money("2.5"))
        );
    }
    
    private static Stream<Arguments> validMultipleSales() {
        return Stream.of(
            Arguments.of("5 sales of apple at 10p", 5, "apple", new Money("10p")),
            Arguments.of("2 sales of orange soda at 1", 2, "orange soda", new Money("1")),
            Arguments.of("4 sales of cherry tomatoes at 2.5", 4, "cherry tomatoes", new Money("2.5"))
        );
    }
    
    private static Stream<Arguments> validAddSub() {
        return Stream.of(
            Arguments.of("add 10p apple", new Money("10p"), "apple"),
            Arguments.of("subtract 5 cherry tomatoes", new Money("5"), "cherry tomatoes")
        );
    }
    
    private static Stream<Arguments> validMultiply() {
        return Stream.of(
            Arguments.of("multiply 3.14 apple", new BigDecimal("3.14"), "apple"),
            Arguments.of("multiply 42 cherry tomatoes", new BigDecimal("42"), "cherry tomatoes")
        );
    }
    
    @ParameterizedTest
    @MethodSource("invalidMesgs")
    void testInvalidMesgs(String mesg) {
        assertFalse(smf.getSaleMessage(mesg).isPresent());
    }
    
    @ParameterizedTest
    @MethodSource("validSingleSales")
    void testValidSingleSales(String mesg, String prod, Money price) {
        Optional<SaleMessage> sm = smf.getSaleMessage(mesg);
        assertTrue(sm.isPresent());
        assertTrue(sm.get() instanceof ProductSaleMessage);
        ProductSaleMessage psm = (ProductSaleMessage) sm.get();
        assertTrue(psm.getQty() == 1);
        assertEquals(prod, psm.getProduct());
        assertEquals(price, psm.getPrice());
    }
    
    @ParameterizedTest
    @MethodSource("validMultipleSales")
    void testValidMultipleSales(String mesg, Integer qty, String prod, Money price) {
        Optional<SaleMessage> sm = smf.getSaleMessage(mesg);
        assertTrue(sm.isPresent());
        assertTrue(sm.get() instanceof ProductSaleMessage);
        ProductSaleMessage psm = (ProductSaleMessage) sm.get();
        assertEquals(qty, psm.getQty());
        assertEquals(price, psm.getPrice());
        assertEquals(prod, psm.getProduct());
    }
    
    @ParameterizedTest
    @MethodSource("validAddSub")
    void testValidAddSub(String mesg, Money adj, String prod) {
        Optional<SaleMessage> sm = smf.getSaleMessage(mesg);
        assertTrue(sm.isPresent());
        assertTrue(sm.get() instanceof PriceAdjustmentMessage);
        PriceAdjustmentMessage pam = (PriceAdjustmentMessage) sm.get();
        assertTrue(pam.isAddSub());
        assertEquals(adj, pam.getPrice());
        assertEquals(prod, pam.getProduct());
    }
    
    @ParameterizedTest
    @MethodSource("validMultiply")
    void testValidMultiply(String mesg, BigDecimal multiplier, String prod) {
        Optional<SaleMessage> sm = smf.getSaleMessage(mesg);
        assertTrue(sm.isPresent());
        assertTrue(sm.get() instanceof PriceAdjustmentMessage);
        PriceAdjustmentMessage pam = (PriceAdjustmentMessage) sm.get();
        assertTrue(pam.isMultiply());
        assertEquals(multiplier, pam.getMultiplier());
        assertEquals(prod, pam.getProduct());
    }
}