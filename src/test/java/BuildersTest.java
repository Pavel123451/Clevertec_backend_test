import org.junit.jupiter.api.Test;
import ru.clevertec.builder.impl.DiscountCardBuilder;
import ru.clevertec.builder.impl.ProductBuilder;
import ru.clevertec.models.DiscountCard;
import ru.clevertec.models.Product;

import static org.junit.jupiter.api.Assertions.*;

public class BuildersTest {
    @Test
    void testProductBuilder() {
        Product product = new ProductBuilder()
                .setId(1L)
                .setDescription("Test Product")
                .setPrice(10.99)
                .setQuantityInStock(100)
                .setWholesaleProduct(true)
                .build();

        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getDescription());
        assertEquals(10.99, product.getPrice());
        assertEquals(100, product.getQuantityInStock());
        assertTrue(product.isWholesaleProduct());
    }

    @Test
    void testDiscountCardBuilder() {
        DiscountCard discountCard = new DiscountCardBuilder()
                .setId(1L)
                .setNumber(1111)
                .setDiscountPercentage((short) 10)
                .build();

        assertNotNull(discountCard);
        assertEquals(1L, discountCard.getId());
        assertEquals(1111, discountCard.getNumber());
        assertEquals(10, discountCard.getDiscountPercentage());
    }
}
