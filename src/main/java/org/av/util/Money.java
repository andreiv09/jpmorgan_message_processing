package org.av.util;

import java.math.BigDecimal;

/**
 * Stores money amount, to make it easier to handle precision and various input formats.
 */
public class Money {
    private static final int SCALE = 2;
    private static final int ROUNDING = BigDecimal.ROUND_HALF_DOWN;

    private final BigDecimal amount;

    /**
     * Tries to parse amt as either GBP nn.nn decimals optional or pence nnp
     *
     * @throws IllegalArgumentException if amt is not parsable
     */
    public Money(String amt) {
        if (null == amt || amt.isEmpty()) {
            throw new IllegalArgumentException("invalid amount");
        }

        try {
            if (isPenceAmount(amt)) {
                amount = new BigDecimal(String.format("%.2f",
                    Double.parseDouble(amt.substring(0, amt.length() - 1)) / 100.0)).setScale(SCALE, ROUNDING);
            } else {
                amount = new BigDecimal(amt).setScale(SCALE, ROUNDING);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("unparsable amount", e);
        }
    }

    private Money(BigDecimal amt) {
        if (null == amt) {
            throw new IllegalArgumentException("invalid amount");
        }

        this.amount = new BigDecimal(amt.toString());
    }

    private boolean isPenceAmount(String amt) {
        return amt.matches("^(?:-)?\\d+p$");
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Money && this.amount.equals(((Money) other).amount);
    }

    @Override
    public String toString() {
        return amount.toString();
    }

    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(amount.multiply(multiplier));
    }

    public Money add(Money amt) {
        return new Money(amount.add(amt.amount));
    }

    public Money subtract(Money amt) {
        return new Money(amount.subtract(amt.amount));
    }
}
