package org.av.message;

import org.av.util.Money;

public class ProductSaleMessage implements SaleMessage {
    private final String product;
    private final Money price;
    private final Integer qty;
    
    public ProductSaleMessage(String product, Money price, Integer qty) {
        this.product = product;
        this.price = price;
        this.qty = qty;
    }
    
    @Override
    public boolean isValid() {
        return MessageValidators.isValidPrice(price) && MessageValidators.isValidProduct(product) && MessageValidators.isValidQuantity(qty);
    }
    
    public String getProduct() {
        return product;
    }
    
    public Money getPrice() {
        return price;
    }
    
    public Integer getQty() {
        return qty;
    }
}
