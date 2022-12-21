package zearch.index;

import org.junit.jupiter.api.Test;
import zearch.gram.Grammifier;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndexDatabaseTest {
    @Test
    void connectionTest() throws SQLException {
        new IndexDatabase();
    }

    @Test
    void readWriteTest() throws SQLException {
        IndexDatabase DB = new IndexDatabase();
        String url = "dbtest.zawie.io"; // fake url
        String content = "sodifndsoifnsdoinfsdoibnf";
        Map<String,Short> gramToCountWrote = Grammifier.grammify(content);

        System.out.println("Writing");
        DB.write(url, gramToCountWrote);

        System.out.println("Reading");
        Map<String,Short> gramToCountRead = DB.read(url);
        assertEquals(gramToCountWrote, gramToCountRead);
    }
}