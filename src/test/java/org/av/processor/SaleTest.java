package org.av.processor;

import org.av.sale.Sale;
import org.av.util.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SaleTest {
    @Test
    void multiply() {
        Sale s = new Sale(new Money("2"), 2);
        s = s.multiplyPrice(new BigDecimal("2"));
        assertTrue(s.isValidSale());
        assertTrue(s.getQty() == 2);
        assertEquals(new Money("8"), s.getValue());
    }

    @Test
    void addSub() {
        Sale s = new Sale(new Money("2"), 2);
        s = s.addSub(new Money("1"), true);
        s = s.addSub(new Money("1"), false);
        assertTrue(s.isValidSale());
        assertEquals(new Money("4"), s.getValue());
        assertTrue(s.getQty() == 2);
        s = s.addSub(new Money("100"), false);
        assertFalse(s.isValidSale());
    }
}