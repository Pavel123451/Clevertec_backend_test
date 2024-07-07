package main.java.ru.clevertec.services;

import main.java.ru.clevertec.exceptions.BadRequestException;
import main.java.ru.clevertec.factories.DiscountCardFactory;
import main.java.ru.clevertec.factories.ProductFactory;
import main.java.ru.clevertec.models.Product;
import main.java.ru.clevertec.models.DiscountCard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvReaderService {
    private final static int PRODUCT_FIELDS_COUNT = 5;
    private final static int DISCOUNT_CARD_FIELDS_COUNT = 3;
    public static List<Product> readProducts(String fileName) throws BadRequestException {
        List<Product> products = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(fileName))) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(";");
                if (parts.length != PRODUCT_FIELDS_COUNT) {
                    throw new BadRequestException("Invalid product data format");
                }
                products.add(ProductFactory.createProduct(ProductFactory.ProductType.STANDARD, parts));
            }
        } catch (FileNotFoundException e) {
            throw new BadRequestException("File with products not found");
        }
        return products;
    }

    public static List<DiscountCard> readDiscountCards(String fileName) throws BadRequestException {
        List<DiscountCard> discountCards = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(fileName))) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(";");
                if (parts.length != DISCOUNT_CARD_FIELDS_COUNT) {
                    throw new BadRequestException("Invalid discount card data format");
                }
                discountCards.add(DiscountCardFactory
                        .createDiscountCard(DiscountCardFactory.DiscountCardType.STANDARD, parts));
            }
        } catch (FileNotFoundException e) {
            throw new BadRequestException("File with discount cards not found");
        }
        return discountCards;
    }
}
