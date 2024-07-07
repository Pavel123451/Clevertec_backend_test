package main.java.ru.clevertec.factories;

import main.java.ru.clevertec.models.Product;

import java.util.Scanner;

public class ProductFactory {
    public static Product createProduct(ProductType type, String[] parts) {
        switch (type) {
            case STANDARD:
                return new Product(parts);
            default:
                throw new IllegalArgumentException("Unknown product type: " + type);
        }
    }

    public enum ProductType {
        STANDARD
    }
}
