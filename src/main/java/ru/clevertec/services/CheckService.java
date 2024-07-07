package main.java.ru.clevertec.services;

import main.java.ru.clevertec.exceptions.BadRequestException;
import main.java.ru.clevertec.exceptions.NotEnoughMoneyException;
import main.java.ru.clevertec.models.DiscountCard;
import main.java.ru.clevertec.models.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

public class CheckService {
    private final static int PRODUCT_ARG_PARTS_COUNT = 2;
    private final static int QTY_FOR_WHOLESALE = 5;
    private List<Product> products;
    private DiscountCard discountCard;
    private double balance;

    public CheckService(List<Product> products, DiscountCard discountCard, double balance) {
        this.products = products;
        this.discountCard = discountCard;
        this.balance = balance;
    }

    private Map<Integer, Integer> parseProductQuantities(String productArgs)
            throws BadRequestException {
        Map<Integer, Integer> productQuantities = new HashMap<>();
        String[] args = productArgs.split(" ");

        for (String arg : args) {
            String[] parts = arg.split("-");
            if (parts.length != PRODUCT_ARG_PARTS_COUNT) {
                throw new BadRequestException("Invalid product argument format: " + arg);
            }
            int id = 0;
            int quantity = 0;
            try {
                id = Integer.parseInt(parts[0]);
                quantity = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                throw new BadRequestException("Invalid product argument format: " + arg);
            }
            if (quantity <= 0) {
                throw new BadRequestException("Invalid quantity: " + quantity);
            }
            productQuantities.merge(id, quantity, Integer::sum);
        }
        return productQuantities;
    }

    private BigDecimal calculateDiscount(Product product, int quantity, StringBuilder discountDetails) {
        BigDecimal discount = BigDecimal.ZERO;

        if (product.isWholesaleProduct() && quantity >= QTY_FOR_WHOLESALE) {
            discount = BigDecimal.valueOf(product.getPrice())
                    .multiply(BigDecimal.valueOf(quantity))
                    .multiply(BigDecimal.valueOf(0.1));
            discountDetails.append("Wholesale discount applied: ").append(discount).append("$");
        } else if (discountCard != null) {
            discount = BigDecimal.valueOf(product.getPrice())
                    .multiply(BigDecimal.valueOf(quantity))
                    .multiply(BigDecimal.valueOf(discountCard.getDiscountPercentage()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            discountDetails.append("Discount card applied: ").append(discount).append("$");
        }

        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    public String generateCheck(Map<String, String> params)
            throws BadRequestException, NotEnoughMoneyException {
        String productArgs = params.get("productArgs");
        Map<Integer, Integer> productQuantities = parseProductQuantities(productArgs);

        StringBuilder csvContent = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();

        csvContent.append("Date;Time\n");
        csvContent.append(dateFormat.format(now)).append(";")
                .append(timeFormat.format(now)).append("\n\n");
        System.out.println("Date: " + dateFormat.format(now));
        System.out.println("Time: " + timeFormat.format(now));
        System.out.println();

        csvContent.append("QTY;DESCRIPTION;PRICE;DISCOUNT;TOTAL\n");
        System.out.println("QTY\tDESCRIPTION\tPRICE\tDISCOUNT\tTOTAL");

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalWithoutDiscount = BigDecimal.ZERO;

        for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
            int id = entry.getKey();
            int quantity = entry.getValue();

            Product product = products.stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Product with id " + id + " not found"));

            if (quantity > product.getQuantityInStock()) {
                throw new BadRequestException("Not enough stock for product id: " + id);
            }

            BigDecimal productPrice = BigDecimal.valueOf(product.getPrice());
            BigDecimal productTotalCost = productPrice.multiply(BigDecimal.valueOf(quantity));
            StringBuilder discountDetails = new StringBuilder();
            BigDecimal discount = calculateDiscount(product, quantity, discountDetails);

            BigDecimal costAfterDiscount = productTotalCost.subtract(discount);

            csvContent.append(String.format("%d;%s;%.2f$;%.2f$;%.2f$\n",
                    quantity, product.getDescription(),
                    productPrice, discount, productTotalCost));
            System.out.printf("%d\t%s\t%.2f$\t%.2f$\t%.2f$%n", quantity,
                    product.getDescription(), productPrice, discount, productTotalCost);
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println(discountDetails);
            }

            totalCost = totalCost.add(costAfterDiscount);
            totalDiscount = totalDiscount.add(discount);
            totalWithoutDiscount = totalWithoutDiscount.add(productTotalCost);
        }

        if (discountCard != null) {
            csvContent.append("\nDISCOUNT CARD;DISCOUNT PERCENTAGE\n");
            csvContent.append(discountCard.getNumber()).append(";")
                    .append(discountCard.getDiscountPercentage()).append("%\n");
            System.out.println();
            System.out.println("DISCOUNT CARD: " + discountCard.getNumber());
            System.out.println("DISCOUNT PERCENTAGE: " + discountCard.getDiscountPercentage() + "%");
        }

        if (balance < totalCost.doubleValue()) {
            throw new NotEnoughMoneyException("Not enough money on the debit card");
        }

        csvContent.append("\nTOTAL PRICE;TOTAL DISCOUNT;TOTAL WITH DISCOUNT\n");
        csvContent.append(String.format("%.2f$;%.2f$;%.2f$\n",
                totalWithoutDiscount, totalDiscount, totalCost));
        System.out.println();
        System.out.println("TOTAL PRICE: " + totalWithoutDiscount + "$");
        System.out.println("TOTAL DISCOUNT: " + totalDiscount + "$");
        System.out.println("TOTAL WITH DISCOUNT: " + totalCost + "$");

        return csvContent.toString();
    }
}
