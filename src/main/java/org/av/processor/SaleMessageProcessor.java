package org.av.processor;

import org.av.message.PriceAdjustmentMessage;
import org.av.message.ProductSaleMessage;
import org.av.message.SaleMessage;
import org.av.message.SaleMessageFactory;
import org.av.sale.SaleDataStore;

public class SaleMessageProcessor {
    private final SaleMessageFactory smf;
    private final SaleDataStore store;

    public SaleMessageProcessor(SaleDataStore store) {
        smf = new SaleMessageFactory();
        this.store = store;
    }

    /**
     * Handles decoding, validation and storage of a sale message
     * All data is stored in the DataStore provided on object construction
     *
     * @param mesg - message line to process
     */
    public void process(String mesg) {
        smf.getSaleMessage(mesg).ifPresent(this::applyMessage);
    }

    private void applyMessage(SaleMessage mesg) {
        if (!mesg.isValid()) {
            return;
        }

        if (mesg instanceof ProductSaleMessage) {
            ProductSaleMessage ps = (ProductSaleMessage) mesg;
            store.recordSale(ps.getProduct(), ps.getPrice(), ps.getQty());
        }

        if (mesg instanceof PriceAdjustmentMessage) {
            PriceAdjustmentMessage pa = (PriceAdjustmentMessage) mesg;
            if (pa.isMultiply()) {
                store.recordMultiply(pa.getProduct(), pa.getMultiplier());
            }
            if (pa.isAddSub()) {
                store.recordAddSub(pa.getProduct(), pa.getPrice(), pa.isPriceIncrease());
            }
        }
    }
}
