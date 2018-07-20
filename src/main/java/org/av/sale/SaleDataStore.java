package org.av.sale;

import org.av.message.SaleAdjustmentOp;
import org.av.util.Money;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A data storage for recording sales and adjustments, and for generating reports
 */
public class SaleDataStore {

    private final Map<String, List<Sale>> saleStore;
    private final Map<String, List<SaleAdjustmentReport>> saleAdjustments;

    public SaleDataStore() {
        saleStore = new HashMap<>();
        saleAdjustments = new HashMap<>();
    }

    public Map<String, SaleReport> getSaleReport() {
        Map<String, SaleReport> saleReport = new HashMap<>();
        for (String prod : saleStore.keySet()) {
            List<Sale> sales = saleStore.get(prod);
            Money totalVal = sales.stream().map(Sale::getValue).reduce(new Money("0"), Money::add);
            Integer totalQty = sales.stream().map(Sale::getQty).reduce(0, (a, b) -> a + b);
            saleReport.putIfAbsent(prod, new SaleReport(totalVal, totalQty));
        }
        return saleReport;
    }

    public Map<String, List<SaleAdjustmentReport>> getAdjustmentReport() {
        return saleAdjustments;
    }

    public void recordSale(String product, Money price, Integer qty) {
        saleStore.putIfAbsent(product, new ArrayList<>());
        saleStore.get(product).add(new Sale(price, qty));
    }

    public void recordMultiply(String product, BigDecimal multiplier) {
        if (!saleStore.containsKey(product)) {
            return;
        }
        if (!isValidMultiply(multiplier)) {
            return;
        }

        List<Sale> sales = saleStore.get(product);
        saleStore.put(product, sales.stream().map(s -> s.multiplyPrice(multiplier)).collect(Collectors.toList()));

        saleAdjustments.putIfAbsent(product, new ArrayList<>());
        saleAdjustments.get(product).add(new SaleAdjustmentReport(SaleAdjustmentOp.MULTIPLY.toString(), multiplier.toString()));
    }

    private boolean isValidMultiply(BigDecimal multiplier) {
        return (null != multiplier && multiplier.compareTo(BigDecimal.ZERO) > 0);
    }

    public void recordAddSub(String product, Money amount, boolean isPriceIncrease) {
        if (!saleStore.containsKey(product)) {
            return;
        }
        List<Sale> sales = saleStore.get(product);
        List<Sale> chgs = sales.stream().map(s -> s.addSub(amount, isPriceIncrease)).collect(Collectors.toList());
        Optional<Sale> validOp = chgs.stream().filter(s -> !s.isValidSale()).findFirst();
        if (validOp.isPresent()) {
            return;
        }
        saleStore.put(product, chgs);

        saleAdjustments.putIfAbsent(product, new ArrayList<>());
        saleAdjustments.get(product).add(new SaleAdjustmentReport(
            isPriceIncrease ?
                SaleAdjustmentOp.ADD.toString() : SaleAdjustmentOp.SUBTRACT.toString(),
            amount.toString()));
    }
}
