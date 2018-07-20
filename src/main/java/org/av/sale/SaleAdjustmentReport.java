package org.av.sale;

/**
 * Simple storage for adjustments on a sale, used for generating a sale report
 */
public class SaleAdjustmentReport {
    private final String op;
    private final String modifier;

    public SaleAdjustmentReport(String op, String modifier) {
        this.op = op;
        this.modifier = modifier;
    }

    public String getOp() {
        return op;
    }

    public String getModifier() {
        return modifier;
    }
}
