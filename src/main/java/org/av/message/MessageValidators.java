package org.av.message;

import org.av.util.Money;

import java.math.BigDecimal;

public class MessageValidators {
    public static boolean isValidProduct(String product) {
        return (null != product && !product.isEmpty());
    }
    
    public static boolean isValidPrice(Money price) {
        return (null != price && price.compareTo(new Money("0")) >= 0);
    }
    
    public static boolean isValidPriceAdjustment(Money price) {
        return (null != price);
    }
    
    public static boolean isValidQuantity(Integer quantity) {
        return (null != quantity && quantity >= 0);
    }
    
    public static boolean isValidMultiplier(BigDecimal multiplier) {
        return (null != multiplier);
    }
}
