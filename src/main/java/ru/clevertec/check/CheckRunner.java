package ru.clevertec.check;

import ru.clevertec.dao.ConnectionPoolManager;
import ru.clevertec.dao.Dao;
import ru.clevertec.dao.impl.DiscountCardDao;
import ru.clevertec.dao.impl.ProductDao;
import ru.clevertec.exceptions.BadRequestException;
import ru.clevertec.exceptions.NotEnoughMoneyException;
import ru.clevertec.models.DiscountCard;
import ru.clevertec.models.Product;
import ru.clevertec.services.CheckService;
import ru.clevertec.utils.CsvUtil;

import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckRunner {
    private final static String DEFAULT_RESULT_FILE_PATH = "result.csv";

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        String saveToFile = DEFAULT_RESULT_FILE_PATH;
        try {
            Map<String, String> params = CsvUtil.parseArgs(args);
            saveToFile = params.get("saveToFile");
            if (saveToFile == null || saveToFile.trim().isEmpty()) {
                saveToFile = DEFAULT_RESULT_FILE_PATH;
                throw new BadRequestException("Path to output file is missing");
            }

            String url = params.get("datasource.url");
            String username = params.get("datasource.username");
            String password = params.get("datasource.password");

            if (url == null || username == null || password == null) {
                throw new BadRequestException("Missing database connection parameters.");
            }
            ConnectionPoolManager connectionPoolManager =
                    new ConnectionPoolManager(url, username, password, 1);

            try (Connection connection = connectionPoolManager.getConnection()) {
                Dao<Product> productDao = new ProductDao(connection);
                Dao<DiscountCard> discountCardDao = new DiscountCardDao(connection);

                List<Product> products = productDao.getAll();
                List<DiscountCard> discountCards = discountCardDao.getAll();

                String discountCardNumber = params.get("discountCard");
                String balanceStr = params.get("balanceDebitCard");

                DiscountCard discountCard = discountCards.stream()
                        .filter(card -> card.getNumber() == Integer.parseInt(discountCardNumber))
                        .findFirst()
                        .orElse(null);

                double balance = Double.parseDouble(balanceStr);

                CheckService checkService = new CheckService(products, discountCard, balance);

                CsvUtil.writeToCsv(saveToFile, checkService.generateCheck(params));
            } catch (NotEnoughMoneyException e) {
                System.err.println(e.getMessage());
                CsvUtil.writeToCsv(saveToFile, "ERROR\nNOT ENOUGH MONEY");
            } finally {
                connectionPoolManager.closeAllConnections();
            }
        } catch (BadRequestException e) {
            System.err.println(e.getMessage());
            CsvUtil.writeToCsv(saveToFile, "ERROR\nBAD REQUEST");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            CsvUtil.writeToCsv(saveToFile, "ERROR\nINTERNAL SERVER ERROR");
        }
    }
}
