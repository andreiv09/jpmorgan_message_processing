package org.av.processor;

import org.av.sale.SaleAdjustmentReport;
import org.av.sale.SaleDataStore;
import org.av.sale.SaleReport;
import org.av.util.Money;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaleMessageProcessorTest {
    @Test
    void process() {
        SaleDataStore sds = new SaleDataStore();
        SaleMessageProcessor smp = new SaleMessageProcessor(sds);
        List<String> saleMessages = Arrays.asList(
            "error",
            "3 sales of apple at 2",
            "pear at 1.05",
            "add 1 apple",
            "subtract 5p pear",
            "3 sales of wrapping paper at 2.5",
            "3 sales of wrapping paper at 2.5",
            "add 3p wrapping paper"
        );

        for (String sm : saleMessages) {
            smp.process(sm);
        }

        Map<String, SaleReport> sr = sds.getSaleReport();
        Map<String, List<SaleAdjustmentReport>> sar = sds.getAdjustmentReport();

        assertTrue(sr.containsKey("apple"));
        assertTrue(sr.containsKey("pear"));
        assertTrue(sr.containsKey("wrapping paper"));
        assertTrue(3 == sr.get("apple").getQty());
        assertEquals(new Money("9"), sr.get("apple").getValue());
        assertTrue(1 == sr.get("pear").getQty());
        assertEquals(new Money("1.0"), sr.get("pear").getValue());
        assertTrue(6 == sr.get("wrapping paper").getQty());
        assertEquals(new Money("15.18"), sr.get("wrapping paper").getValue());
    }
}