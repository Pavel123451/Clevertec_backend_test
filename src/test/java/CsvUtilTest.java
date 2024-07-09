import org.junit.jupiter.api.Test;
import ru.clevertec.exceptions.BadRequestException;
import ru.clevertec.utils.CsvUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsvUtilTest {

    @Test
    void testParseArgs_withValidArgs() throws Exception {
        String[] args = {"1-2", "3-4", "discountCard=1111", "balanceDebitCard=100"};
        Map<String, String> params = CsvUtil.parseArgs(args);

        assertEquals("1-2 3-4", params.get("productArgs"));
        assertEquals("1111", params.get("discountCard"));
        assertEquals("100", params.get("balanceDebitCard"));
    }

    @Test
    void testParseArgs_missingProductArgs() {
        String[] args = {"discountCard=1111", "balanceDebitCard=100"};

        Exception exception = assertThrows(BadRequestException.class, () -> {
            CsvUtil.parseArgs(args);
        });

        assertEquals("Missing products args", exception.getMessage());
    }

    @Test
    void testParseArgs_missingBalanceDebitCard() {
        String[] args = {"1-2", "3-4", "discountCard=1111"};

        Exception exception = assertThrows(BadRequestException.class, () -> {
            CsvUtil.parseArgs(args);
        });

        assertEquals("Missing balanceDebitCard arg", exception.getMessage());
    }

    @Test
    void testWriteToCsv_success() throws IOException {
        String content = "Test content";
        String testFilePath = "test_output.csv";
        CsvUtil.writeToCsv(testFilePath, content);

        String fileContent = new String(Files.readAllBytes(Paths.get(testFilePath)));
        assertEquals(content, fileContent);
    }

    @Test
    void testWriteToCsv_fileNotFound() {
        String invalidFilePath = "/invalid_path/test_output.csv";
        String content = "Test content";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            CsvUtil.writeToCsv(invalidFilePath, content);
        });

        assertTrue(exception.getMessage().contains("Error writing to CSV file"));
    }
}