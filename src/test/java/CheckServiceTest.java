import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.dao.impl.ProductDao;
import ru.clevertec.exceptions.BadRequestException;
import ru.clevertec.exceptions.NotEnoughMoneyException;
import ru.clevertec.models.DiscountCard;
import ru.clevertec.models.Product;
import ru.clevertec.services.CheckService;
import ru.clevertec.servlets.CheckServlet;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CheckServiceTest {
    private CheckService checkService;
    private ProductDao productDaoMock;
    private DiscountCard discountCard;

    @BeforeEach
    public void setUp() {
        productDaoMock = mock(ProductDao.class);
        discountCard = new DiscountCard(1L, 10,(short) 1);
        List<Product> products = Arrays.asList(
                new Product(1L, "Product 1", 100, 5, true),
                new Product(2L, "Product 2", 50, 10, false)
        );
        checkService = new CheckService(products, discountCard, 1000);
    }

    @Test
    public void testGenerateCheck_withSufficientBalance() throws SQLException, BadRequestException, NotEnoughMoneyException {
        CheckServlet.CheckRequest.ProductQuantity pq1 = new CheckServlet.CheckRequest.ProductQuantity();
        pq1.setId(1);
        pq1.setQuantity(2);

        CheckServlet.CheckRequest.ProductQuantity pq2 = new CheckServlet.CheckRequest.ProductQuantity();
        pq2.setId(2);
        pq2.setQuantity(3);

        List<CheckServlet.CheckRequest.ProductQuantity> productQuantities = Arrays.asList(pq1, pq2);

        String check = checkService.generateCheck(productQuantities, productDaoMock);
        assertNotNull(check);
        assertTrue(check.contains("TOTAL PRICE"));
    }

    @Test
    public void testGenerateCheck_withInsufficientBalance() {
        checkService = new CheckService(Arrays.asList(), discountCard, 1);

        CheckServlet.CheckRequest.ProductQuantity pq1 = new CheckServlet.CheckRequest.ProductQuantity();
        pq1.setId(1);
        pq1.setQuantity(2);

        List<CheckServlet.CheckRequest.ProductQuantity> productQuantities = Arrays.asList(pq1);

        assertThrows(BadRequestException.class, () -> {
            checkService.generateCheck(productQuantities, productDaoMock);
        });
    }

    @Test
    public void testValidateProducts_withInsufficientStock() {
        CheckServlet.CheckRequest.ProductQuantity pq1 = new CheckServlet.CheckRequest.ProductQuantity();
        pq1.setId(1);
        pq1.setQuantity(10); // Quantity exceeds stock

        List<CheckServlet.CheckRequest.ProductQuantity> productQuantities = Arrays.asList(pq1);

        assertThrows(BadRequestException.class, () -> {
            checkService.generateCheck(productQuantities, productDaoMock);
        });
    }
}