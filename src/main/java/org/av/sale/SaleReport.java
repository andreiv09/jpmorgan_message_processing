package org.av.sale;

import org.av.util.Money;

/**
 * Storage of basic sale information (total value and quantity)
 */
public class SaleReport {
    private final Money value;
    private final Integer qty;

    public SaleReport(Money value, Integer qty) {
        this.value = value;
        this.qty = qty;
    }

    public Money getValue() {
        return value;
    }

    public Integer getQty() {
        return qty;
    }
}
