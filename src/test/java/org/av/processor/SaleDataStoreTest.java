package org.av.processor;

import org.av.message.SaleAdjustmentOp;
import org.av.sale.SaleAdjustmentReport;
import org.av.sale.SaleDataStore;
import org.av.sale.SaleReport;
import org.av.util.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SaleDataStoreTest {
    private SaleDataStore sds;

    @BeforeEach
    void initEach() {
        sds = new SaleDataStore();
    }

    @Test
    void recordSale() {
        sds.recordSale("apple", new Money("2"), 2);
        Map<String, SaleReport> sr = sds.getSaleReport();
        assertTrue(sr.containsKey("apple"));
        assertTrue(sr.get("apple").getQty() == 2);
        assertEquals(new Money("4"), sr.get("apple").getValue());
    }

    @Test
    void recordMultiply() {
        sds.recordSale("apple", new Money("2"), 2);
        sds.recordMultiply("apple", new BigDecimal("2"));
        sds.recordMultiply("apple", null);
        Map<String, SaleReport> sr = sds.getSaleReport();
        assertTrue(sr.containsKey("apple"));
        assertTrue(sr.get("apple").getQty() == 2);
        assertEquals(new Money("8"), sr.get("apple").getValue());
        sds.recordMultiply("pear", new BigDecimal("2"));
        sr = sds.getSaleReport();
        assertFalse(sr.containsKey("pear"));
        Map<String, List<SaleAdjustmentReport>> sar = sds.getAdjustmentReport();
        assertTrue(sar.containsKey("apple"));
        SaleAdjustmentReport apple_adj = sar.get("apple").get(0);
        assertEquals(SaleAdjustmentOp.MULTIPLY.toString(), apple_adj.getOp());
        assertEquals("2", apple_adj.getModifier());
    }

    @Test
    void recordAddSub() {
        sds.recordSale("apple", new Money("2"), 2);
        sds.recordAddSub("apple", new Money("2"), true);
        sds.recordAddSub("apple", new Money("1"), false);
        sds.recordAddSub("apple", new Money("100"), false);
        Map<String, SaleReport> sr = sds.getSaleReport();
        assertTrue(sr.containsKey("apple"));
        assertTrue(sr.get("apple").getQty() == 2);
        assertEquals(new Money("6"), sr.get("apple").getValue());

        sds.recordAddSub("pear", new Money("2"), true);
        sr = sds.getSaleReport();
        assertFalse(sr.containsKey("pear"));

        Map<String, List<SaleAdjustmentReport>> sar = sds.getAdjustmentReport();
        assertTrue(sar.containsKey("apple"));
        SaleAdjustmentReport apple_adj1 = sar.get("apple").get(0);
        assertEquals(SaleAdjustmentOp.ADD.toString(), apple_adj1.getOp());
        assertEquals("2.00", apple_adj1.getModifier());
        SaleAdjustmentReport apple_adj2 = sar.get("apple").get(1);
        assertEquals(SaleAdjustmentOp.SUBTRACT.toString(), apple_adj2.getOp());
        assertEquals("1.00", apple_adj2.getModifier());
    }
}