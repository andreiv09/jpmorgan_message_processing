package org.av.message;

import org.av.util.Money;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaleMessageFactory {

    private final Logger log = Logger.getLogger(SaleMessageFactory.class.getName());

    private final String priceRegex = "\\d+(?:\\.\\d+)?|\\d+p";
    private final String multiplierRegex = "\\d+(?:\\.\\d+)?";

    private final String saleMessageRegex = "^([^\\d].*)\\s+at\\s+(" + priceRegex + ")$";

    private final String multipleSaleMessageRegex = "^(\\d+)\\s+sales\\s+of\\s+(.+)\\s+at\\s+(" + priceRegex + ")$";

    private final String addSubMessageRegex = "^(add|subtract)\\s+(" + priceRegex + ")\\s+(.+)$";
    private final String multiplyMessageRegex = "^multiply\\s+(" + multiplierRegex + ")\\s+(.+)$";

    private final Pattern saleMesgPattern = Pattern.compile(saleMessageRegex, Pattern.CASE_INSENSITIVE);
    private final Pattern multipleSaleMesgPattern = Pattern.compile(multipleSaleMessageRegex, Pattern.CASE_INSENSITIVE);
    private final Pattern addSubMesgPattern = Pattern.compile(addSubMessageRegex, Pattern.CASE_INSENSITIVE);
    private final Pattern multiplyMesgPattern = Pattern.compile(multiplyMessageRegex, Pattern.CASE_INSENSITIVE);

    /**
     * Expects a sale message as a string. Returns a SaleMessage of the appropriate type wrapped in an Optional.
     * <p>
     * Message formats:
     * <ul>
     * <li>product at price</li>
     * <li>number product at price</li>
     * <li>add|subtract price product</li>
     * <li>multiply number product</li>
     * </ul>
     *
     * @param message - the sale message to parse
     * @return empty Optional on error or an Optional of SaleMessage on parse success
     */
    public Optional<SaleMessage> getSaleMessage(String message) {
        log.fine("Decode message: *" + (message == null ? "[null]" : message) + "*");

        if (null == message || message.isEmpty()) {
            return Optional.empty();
        }

        Matcher saleMesgMatcher = saleMesgPattern.matcher(message);
        Matcher multipleSaleMesgMatcher = multipleSaleMesgPattern.matcher(message);
        Matcher addSubMesgMatcher = addSubMesgPattern.matcher(message);
        Matcher multiplyMesgMatcher = multiplyMesgPattern.matcher(message);

        boolean isSale = saleMesgMatcher.matches();
        boolean isMultipleSale = multipleSaleMesgMatcher.matches();
        boolean isAddSubAdj = addSubMesgMatcher.matches();
        boolean isMultiplyAdj = multiplyMesgMatcher.matches();

        if (!isSale && !isMultipleSale && !isAddSubAdj && !isMultiplyAdj) {
            return Optional.empty();
        }

        final String prod;
        final Optional<Money> price;

        if (isAddSubAdj) {
            //1 = op 2 = price 3 = prod
            final Boolean add = addSubMesgMatcher.group(1).equals("add");
            price = tryOptional(addSubMesgMatcher.group(2), Money::new);
            prod = addSubMesgMatcher.group(3);
            return price.map(p -> new PriceAdjustmentMessage(prod, p, add));
        } else if (isMultiplyAdj) {
            //1 = multiplier 2 = prod
            final Optional<BigDecimal> multiplier = tryOptional(multiplyMesgMatcher.group(1), BigDecimal::new);
            prod = multiplyMesgMatcher.group(2);
            return multiplier.map(m -> new PriceAdjustmentMessage(prod, m));
        } else {
            final Optional<Integer> qty;
            if (isSale) {
                //1 = prod 2 = price
                prod = saleMesgMatcher.group(1);
                price = tryOptional(saleMesgMatcher.group(2), Money::new);
                qty = Optional.of(1);
            } else {
                //1 = quantity 2 = prod 3 = price
                qty = tryOptional(multipleSaleMesgMatcher.group(1), Integer::new);
                prod = multipleSaleMesgMatcher.group(2);
                price = tryOptional(multipleSaleMesgMatcher.group(3), Money::new);
            }
            return price.flatMap(p -> qty.flatMap(q -> Optional.of(new ProductSaleMessage(prod, p, q))));
        }
    }

    private <T> Optional<T> tryOptional(String arg, Function<String, T> converter) {
        try {
            return Optional.of(converter.apply(arg));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
