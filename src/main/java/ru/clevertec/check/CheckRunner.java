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
    private final static String DISCOUNT_CARDS_FILE_PATH = "./src/main/resources/discountCards.csv";
    private final static String DEFAULT_RESULT_FILE_PATH = "result.csv";
    public static void main(String[] args) {
        String pathToFile;
        String saveToFile = DEFAULT_RESULT_FILE_PATH;
        Locale.setDefault(Locale.US);
        try {
            Map<String, String> params = CsvUtil.parseArgs(args);

            pathToFile = params.get("pathToFile");
            saveToFile = params.get("saveToFile");

            if (pathToFile == null || pathToFile.trim().isEmpty()) {
                throw new BadRequestException("Path to input file is missing");
            }
            if (saveToFile == null || saveToFile.trim().isEmpty()) {
                saveToFile = DEFAULT_RESULT_FILE_PATH;
                throw new BadRequestException("Path to output file is missing");
            }


            List<Product> products = CsvReaderService.readProducts(pathToFile);
            List<DiscountCard> discountCards = CsvReaderService.readDiscountCards(DISCOUNT_CARDS_FILE_PATH);

            String discountCardNumber = params.get("discountCard");
            String balanceStr = params.get("balanceDebitCard");

            DiscountCard discountCard = discountCards.stream()
                    .filter(card -> card.getNumber().equals(discountCardNumber))
                    .findFirst()
                    .orElse(null);

            double balance = Double.parseDouble(balanceStr);

            CheckService checkService = new CheckService(products, discountCard, balance);

            CsvUtil.writeToCsv(saveToFile, checkService.generateCheck(params));
        } catch (BadRequestException e) {
            System.err.println(e.getMessage());
            CsvUtil.writeToCsv(saveToFile, "ERROR\nBAD REQUEST");
        } catch (NotEnoughMoneyException e) {
            System.err.println(e.getMessage());
            CsvUtil.writeToCsv(saveToFile, "ERROR\nNOT ENOUGH MONEY");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            CsvUtil.writeToCsv(saveToFile, "ERROR\nINTERNAL SERVER ERROR");
        }
    }
}
