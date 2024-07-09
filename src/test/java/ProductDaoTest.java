import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.clevertec.builder.impl.ProductBuilder;
import ru.clevertec.dao.Dao;
import ru.clevertec.dao.impl.ProductDao;
import ru.clevertec.models.Product;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductDaoTest {

    private static Connection connection;
    private static ProductDao productDao;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE public.product (" +
                "id BIGSERIAL PRIMARY KEY," +
                "description VARCHAR(50) NOT NULL," +
                "price DECIMAL(10, 2) NOT NULL CHECK (price >= 0)," +
                "quantity_in_stock INTEGER NOT NULL CHECK (quantity_in_stock >= 0)," +
                "wholesale_product BOOLEAN NOT NULL DEFAULT FALSE)");
        productDao = new ProductDao(connection);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DROP TABLE public.product");
        connection.close();
    }

    @Test
    void testMapResultSetToProduct() throws Exception {
        productDao.clear();
        Statement stmt = connection.createStatement();
        stmt.execute("INSERT INTO public.product (description, " +
                "price, " +
                "quantity_in_stock, " +
                "wholesale_product) " +
                "VALUES ('Test Product', 10.99, 100, true)");

        ResultSet rs = stmt.executeQuery("SELECT * FROM public.product");

        if (rs.next()) {
            Method method = ProductDao.class.getDeclaredMethod("mapResultSetToProduct", ResultSet.class);
            method.setAccessible(true);
            Product product = (Product) method.invoke(productDao, rs);

            assertEquals("Test Product", product.getDescription());
            assertEquals(10.99, product.getPrice());
            assertEquals(100, product.getQuantityInStock());
            assertTrue(product.isWholesaleProduct());
        }
    }

    @Test
    void testSave() {
        productDao.clear();
        Product product = new ProductBuilder()
                .setDescription("Test Product")
                .setPrice(10.99)
                .setQuantityInStock(100)
                .setWholesaleProduct(true)
                .build();

        productDao.save(product);
        List<Product> products = productDao.getAll();
        assertEquals(1, products.size());
        assertEquals("Test Product", products.getFirst().getDescription());
    }

    @Test
    void testGetById() {
        productDao.clear();
        Product product = new ProductBuilder()
                .setDescription("Test Product")
                .setPrice(10.99)
                .setQuantityInStock(100)
                .setWholesaleProduct(true)
                .build();

        productDao.save(product);
        Product savedProduct = productDao.getAll().getFirst();
        Product fetchedProduct = productDao.getById(savedProduct.getId());

        assertNotNull(fetchedProduct);
        assertEquals("Test Product", fetchedProduct.getDescription());
    }

    @Test
    void testUpdate() {
        productDao.clear();
        Product product = new ProductBuilder()
                .setDescription("Test Product")
                .setPrice(10.99)
                .setQuantityInStock(100)
                .setWholesaleProduct(true)
                .build();

        productDao.save(product);
        Product savedProduct = productDao.getAll().getFirst();
        savedProduct = new ProductBuilder()
                .setId(savedProduct.getId())
                .setDescription("Updated Product")
                .setPrice(15.99)
                .setQuantityInStock(200)
                .setWholesaleProduct(false)
                .build();

        productDao.update(savedProduct);
        Product updatedProduct = productDao.getById(savedProduct.getId());

        assertEquals("Updated Product", updatedProduct.getDescription());
        assertEquals(15.99, updatedProduct.getPrice());
        assertEquals(200, updatedProduct.getQuantityInStock());
        assertFalse(updatedProduct.isWholesaleProduct());
    }

    @Test
    void testDelete() {
        productDao.clear();
        Product product = new ProductBuilder()
                .setDescription("Test Product")
                .setPrice(10.99)
                .setQuantityInStock(100)
                .setWholesaleProduct(true)
                .build();

        productDao.save(product);
        Product savedProduct = productDao.getAll().getFirst();
        productDao.delete(savedProduct.getId());

        assertNull(productDao.getById(savedProduct.getId()));
    }


}
