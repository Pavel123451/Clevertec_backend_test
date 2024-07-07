package main.java.ru.clevertec.check;

import main.java.ru.clevertec.exceptions.BadRequestException;
import main.java.ru.clevertec.exceptions.NotEnoughMoneyException;
import main.java.ru.clevertec.models.DiscountCard;
import main.java.ru.clevertec.models.Product;
import main.java.ru.clevertec.services.CheckService;
import main.java.ru.clevertec.services.CsvReaderService;
import main.java.ru.clevertec.utils.CsvUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckRunner {
    private final static String PRODUCTS_FILE_PATH = "./src/main/resources/products.csv";
    private final static String DISCOUNT_CARDS_FILE_PATH = "./src/main/resources/discountCards.csv";
    private final static String RESULT_FILE_PATH = "result.csv";

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        try {
            Map<String, String> params = CsvUtil.parseArgs(args);
            List<Product> products = CsvReaderService.readProducts(PRODUCTS_FILE_PATH);
            List<DiscountCard> discountCards = CsvReaderService.readDiscountCards(DISCOUNT_CARDS_FILE_PATH);

            String discountCardNumber = params.get("discountCard");
            String balanceStr = params.get("balanceDebitCard");

            DiscountCard discountCard = discountCards.stream()
                    .filter(card -> card.getNumber().equals(discountCardNumber))
                    .findFirst()
                    .orElse(null);

            double balance = Double.parseDouble(balanceStr);

            CheckService checkService = new CheckService(products, discountCard, balance);

            CsvUtil.writeToCsv(RESULT_FILE_PATH, checkService.generateCheck(params));
        } catch (BadRequestException e) {
            System.err.println(e.getMessage());
            CsvUtil.writeToCsv(RESULT_FILE_PATH, "ERROR\nBAD REQUEST");
        } catch (NotEnoughMoneyException e) {
            System.err.println(e.getMessage());
            CsvUtil.writeToCsv(RESULT_FILE_PATH, "ERROR\nNOT ENOUGH MONEY");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            CsvUtil.writeToCsv(RESULT_FILE_PATH, "ERROR\nINTERNAL SERVER ERROR");
        }
    }
}
