
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.clevertec.builder.impl.DiscountCardBuilder;
import ru.clevertec.dao.impl.DiscountCardDao;
import ru.clevertec.models.DiscountCard;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiscountCardDaoTest {

    private static Connection connection;
    private static DiscountCardDao discountCardDao;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE public.discount_card (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "number INTEGER NOT NULL UNIQUE, " +
                "amount SMALLINT NOT NULL CHECK (amount >= 0 AND amount <= 100)" + ")");
        discountCardDao = new DiscountCardDao(connection);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DROP TABLE public.discount_card");
        connection.close();
    }

    @Test
    void testMapResultSetToDiscountCard() throws Exception {
        discountCardDao.clear();
        Statement stmt = connection.createStatement();
        stmt.execute("INSERT INTO public.discount_card (number, amount) VALUES (1111, 10)");

        ResultSet rs = stmt.executeQuery("SELECT * FROM public.discount_card");

        if (rs.next()) {
            Method method = DiscountCardDao.class.getDeclaredMethod("mapResultSetToDiscountCard", ResultSet.class);
            method.setAccessible(true);
            DiscountCard discountCard = (DiscountCard) method.invoke(discountCardDao, rs);

            assertEquals(1111, discountCard.getNumber());
            assertEquals(10, discountCard.getDiscountPercentage());
        }
    }

    @Test
    void testSave() {
        discountCardDao.clear();
        DiscountCard discountCard = new DiscountCardBuilder()
                .setNumber(2222)
                .setDiscountPercentage((short)10)
                .build();

        discountCardDao.save(discountCard);
        List<DiscountCard> discountCards = discountCardDao.getAll();

        assertEquals(1, discountCards.size());
        assertEquals(2222, discountCards.getFirst().getNumber());
    }

    @Test
    void testGetById() {
        discountCardDao.clear();
        DiscountCard discountCard = new DiscountCardBuilder()
                .setNumber(1111)
                .setDiscountPercentage((short)10)
                .build();

        discountCardDao.save(discountCard);
        DiscountCard savedDiscountCard = discountCardDao.getAll().getFirst();
        DiscountCard fetchedDiscountCard = discountCardDao.getById(savedDiscountCard.getId());

        assertNotNull(fetchedDiscountCard);
        assertEquals(1111, fetchedDiscountCard.getNumber());
    }

    @Test
    void testUpdate() {
        discountCardDao.clear();
        DiscountCard discountCard = new DiscountCardBuilder()
                .setNumber(3333)
                .setDiscountPercentage((short)10)
                .build();

        discountCardDao.save(discountCard);
        DiscountCard savedDiscountCard = discountCardDao.getAll().getFirst();
        savedDiscountCard = new DiscountCardBuilder()
                .setId(savedDiscountCard.getId())
                .setNumber(67890)
                .setDiscountPercentage((short)20)
                .build();

        discountCardDao.update(savedDiscountCard);
        DiscountCard updatedDiscountCard = discountCardDao.getById(savedDiscountCard.getId());

        assertEquals(67890, updatedDiscountCard.getNumber());
        assertEquals(20, updatedDiscountCard.getDiscountPercentage());
    }

    @Test
    void testDelete() {
        discountCardDao.clear();
        DiscountCard discountCard = new DiscountCardBuilder()
                .setNumber(4444)
                .setDiscountPercentage((short)10)
                .build();

        discountCardDao.save(discountCard);
        DiscountCard savedDiscountCard = discountCardDao.getAll().getFirst();
        discountCardDao.delete(savedDiscountCard.getId());

        assertNull(discountCardDao.getById(savedDiscountCard.getId()));
    }
}
