package org.av.sale;

import org.av.util.Money;

import java.math.BigDecimal;

/**
 * Stores basic information about a sale: quantity and unit price.
 * Allows basic operations on the sale: increase or decrease price; alter quantity.
 */
public class Sale {
    private final Integer qty;
    private final Money price;

    public Sale(Money price, Integer qty) {
        this.qty = qty;
        this.price = price;
    }

    public Sale multiplyPrice(BigDecimal multiplier) {
        return new Sale(this.price.multiply(multiplier), qty);
    }

    public Sale addSub(Money amt, boolean isAdd) {
        return new Sale(isAdd ? price.add(amt) : price.subtract(amt), qty);
    }

    public boolean isValidSale() {
        return price != null && price.compareTo(new Money("0")) >= 0
            && qty != null && qty > 0;
    }

    public Money getValue() {
        return price.multiply(new BigDecimal(qty));
    }

    public Integer getQty() {
        return qty;
    }
}
