package org.av.message;

import org.av.util.Money;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

public class PriceAdjustmentMessage implements SaleMessage {
    private final String product;
    private final Money price;
    private final BigDecimal multiplier;
    private final SaleAdjustmentOp op;

    public PriceAdjustmentMessage(String product, Money price, boolean add) {
        this.product = product;
        this.price = price;
        this.op = add ? SaleAdjustmentOp.ADD : SaleAdjustmentOp.SUBTRACT;
        this.multiplier = null;
    }

    public PriceAdjustmentMessage(String product, BigDecimal multiplier) {
        this.product = product;
        this.multiplier = multiplier;
        this.op = SaleAdjustmentOp.MULTIPLY;
        this.price = null;
    }

    @Override
    public boolean isValid() {
        boolean valid = false;
        switch (this.op) {
            case ADD:
            case SUBTRACT:
                valid = MessageValidators.isValidProduct(product) && MessageValidators.isValidPriceAdjustment(price);
                break;
            case MULTIPLY:
                valid = MessageValidators.isValidProduct(product) && MessageValidators.isValidMultiplier(multiplier);
                break;
            default:
                break;
        }
        return valid;
    }

    public boolean isAddSub() {
        return Arrays.asList(SaleAdjustmentOp.ADD, SaleAdjustmentOp.SUBTRACT).contains(op);
    }

    public boolean isMultiply() {
        return Collections.singletonList(SaleAdjustmentOp.MULTIPLY).contains(op);
    }

    public String getProduct() {
        return product;
    }

    public Money getPrice() {
        if (!isAddSub()) {
            throw new IllegalStateException("not a price add/sub adjustment");
        }
        return price;
    }

    public boolean isPriceIncrease() {
        if (!isAddSub()) {
            throw new IllegalStateException("not a price add/sub adjustment");
        }
        return op == SaleAdjustmentOp.ADD;
    }

    public BigDecimal getMultiplier() {
        if (!isMultiply()) {
            throw new IllegalStateException("not a price multiplier adjustment");
        }
        return multiplier;
    }
}
