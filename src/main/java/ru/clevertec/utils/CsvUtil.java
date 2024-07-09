package ru.clevertec.utils;


import ru.clevertec.exceptions.BadRequestException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CsvUtil {
    public static Map<String, String> parseArgs(String[] args) throws BadRequestException {
        Map<String, String> params = new HashMap<>();
        StringBuilder productArgs = new StringBuilder();

        for (String arg : args) {
            if (arg.contains("=")) {
                String[] parts = arg.split("=");
                params.put(parts[0], parts[1]);
            } else {
                productArgs.append(arg).append(" ");
            }
        }
        if (!productArgs.isEmpty()) {
            params.put("productArgs", productArgs.toString().trim());
        } else {
            throw new BadRequestException("Missing products args");
        }

        if (!params.containsKey("balanceDebitCard")) {
            throw new BadRequestException("Missing balanceDebitCard arg");
        }

        return params;
    }

    public static void writeToCsv(String filePath, String content) {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.write(content);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error writing to CSV file", e);
        }
    }
}
