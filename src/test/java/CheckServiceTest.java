import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.builder.impl.DiscountCardBuilder;
import ru.clevertec.exceptions.BadRequestException;
import ru.clevertec.exceptions.NotEnoughMoneyException;
import ru.clevertec.models.DiscountCard;
import ru.clevertec.models.Product;
import ru.clevertec.services.CheckService;
import ru.clevertec.utils.CsvUtil;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CheckServiceTest {

    private List<Product> products;
    private DiscountCard discountCard;
    private CheckService checkService;

    @BeforeEach
    void setUp() {
        products = Arrays.asList(
                new Product(1L, "Milk", 1.07, 10, true),
                new Product(2L, "Cream 400g", 2.71, 20, true),
                new Product(3L, "Yogurt 400g", 2.10, 7, true)
        );

        discountCard = new DiscountCardBuilder()
                .setNumber(1111)
                .setDiscountPercentage((short)10)
                .build();
        checkService = new CheckService(products, discountCard, 100.0);
    }

    @Test
    void testParseProductQuantities() throws Exception {
        String productArgs = "1-2 2-3";
        Method method = CheckService.class.getDeclaredMethod("parseProductQuantities", String.class);
        method.setAccessible(true);

        Map<Integer, Integer> productQuantities =
                (Map<Integer, Integer>) method.invoke(checkService, productArgs);

        assertEquals(2, productQuantities.size());
        assertEquals(2, productQuantities.get(1).intValue());
        assertEquals(3, productQuantities.get(2).intValue());
    }

    @Test
    void testParseProductQuantitiesInvalidFormat() {
        String productArgs = "1-2 2";

        Exception exception = assertThrows(Exception.class, () -> {
            Method method = CheckService.class.getDeclaredMethod("parseProductQuantities", String.class);
            method.setAccessible(true);
            method.invoke(checkService, productArgs);
        });

        assertInstanceOf(BadRequestException.class, exception.getCause());
    }

    @Test
    void testCalculateDiscount() throws Exception {
        Product product = products.getFirst();
        int quantity = 6;

        Method method = CheckService.class.getDeclaredMethod("calculateDiscount",
                Product.class, int.class, StringBuilder.class);
        method.setAccessible(true);
        StringBuilder discountDetails = new StringBuilder();

        BigDecimal discount = (BigDecimal) method.invoke(checkService, product, quantity, discountDetails);

        assertEquals(BigDecimal.valueOf(0.64), discount);
        assertTrue(discountDetails.toString().contains("Wholesale discount applied"));
    }

    @Test
    void testGenerateCheck() throws Exception {
        // Подготовка параметров
        Map<String, String> params = new HashMap<>();
        params.put("productArgs", "1-2 2-3");
        params.put("discountCard", "1111");
        params.put("balanceDebitCard", "100");

        String check = checkService.generateCheck(params);

        assertNotNull(check);

        String[] lines = check.split("\n");
        CsvUtil.writeToCsv("./result.csv",check);
        assertEquals("Date;Time", lines[0]);
        assertTrue(lines[1].matches("\\d{2}\\.\\d{2}\\.\\d{4};\\d{2}:\\d{2}:\\d{2}"));
        assertEquals("", lines[2]);

        assertEquals("QTY;DESCRIPTION;PRICE;DISCOUNT;TOTAL", lines[3]);

        assertTrue(lines[4].contains("2;Milk;1,07$;0,21$;2,14$"));
        assertTrue(lines[5].contains("3;Cream 400g;2,71$;0,81$;8,13$"));

        assertEquals("", lines[6]);
        assertEquals("DISCOUNT CARD;DISCOUNT PERCENTAGE", lines[7]);
        assertEquals("1111;10%", lines[8]);

        assertEquals("", lines[9]);
        assertEquals("TOTAL PRICE;TOTAL DISCOUNT;TOTAL WITH DISCOUNT", lines[10]);
        assertTrue(lines[11].contains("10,27$;1,02$;9,25$"));

    }


    @Test
    void testGenerateCheckNotEnoughMoney() {
        CheckService serviceWithLowBalance = new CheckService(products, discountCard, 1.0);

        Map<String, String> params = new HashMap<>();
        params.put("productArgs", "1-2 2-3");
        params.put("discountCard", "1111");
        params.put("balanceDebitCard", "1");

        Exception exception = assertThrows(NotEnoughMoneyException.class, () -> {
            serviceWithLowBalance.generateCheck(params);
        });

        assertEquals("Not enough money on the debit card", exception.getMessage());
    }

    @Test
    void testGenerateCheckInvalidProductArgs() {
        Map<String, String> params = new HashMap<>();
        params.put("productArgs", "1-2 2");
        params.put("discountCard", "1111");
        params.put("balanceDebitCard", "100");

        Exception exception = assertThrows(BadRequestException.class, () -> {
            checkService.generateCheck(params);
        });

        assertTrue(exception.getMessage().contains("Invalid product argument format"));
    }
}