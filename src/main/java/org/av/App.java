package org.av;

import org.av.sale.SaleAdjustmentReport;
import org.av.sale.SaleDataStore;
import org.av.processor.SaleMessageProcessor;
import org.av.sale.SaleReport;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App {
    static {
        //preconfigure logger
        try {
            LogManager.getLogManager().readConfiguration(App.class.getResourceAsStream("/log.properties"));
        } catch (NullPointerException | IOException e) {
            System.out.println("Unable to read log configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static final Logger log = Logger.getLogger(App.class.getName());
    private static final long MESSAGE_LIMIT = 50L;
    private static final long PARTIAL_REPORT_LIMIT = 10L;

    public static void main(String[] args) {
        log.info("Sale message processor starting");

        SaleDataStore store = new SaleDataStore();
        SaleMessageProcessor smp = new SaleMessageProcessor(store);

        Scanner in = new Scanner(System.in);
        long nMsg = 0;
        while (in.hasNextLine() && nMsg < MESSAGE_LIMIT) {
            String mesg = in.nextLine();
            smp.process(mesg);
            nMsg++;
            if (nMsg % PARTIAL_REPORT_LIMIT == 0) {
                log.info("Partial report:\n"
                    + formatSaleReport(store.getSaleReport()));
            }
        }

        log.info("Sale message processor PAUSED");
        log.info("Adjustment report: " + formatSaleAdjustments(store.getAdjustmentReport()));
        log.info("Sale report:\n" + formatSaleReport(store.getSaleReport()));
        log.info("Sale message processor finished");
    }

    private static String formatSaleReport(Map<String, SaleReport> saleRep) {
        String rep = saleRep.keySet().stream().map(
            k -> {
                SaleReport sr = saleRep.get(k);
                return String.format("%15s | %5d | %10s",
                    k,
                    sr.getQty(),
                    sr.getValue().toString());
            }
        ).reduce("", (a, b) -> a + "\n" + b);
        return "----------------+-------+-------------\n" +
                String.format("%15s | %5s | %10s", "Product", "Qty", "Total value") +
                "\n----------------+-------+-------------" +
                rep +
                "\n----------------+-------+-------------";
    }

    private static String formatSaleAdjustments(Map<String, List<SaleAdjustmentReport>> saleAdjs) {
        StringBuilder sb = new StringBuilder();
        for (String prod : saleAdjs.keySet()) {
            sb.append("\nFor product ").append(prod).append("\n");
            sb.append("Operation | Modifier");
            sb.append("\n----------+---------");
            sb.append(
                saleAdjs.get(prod).stream().map(
                    sa -> String.format("%9s | %s", sa.getOp(), sa.getModifier())
                ).reduce("", (a, b) -> a + "\n" + b)
            );
            sb.append("\n----------+---------\n");
        }
        return sb.toString();
    }
}
