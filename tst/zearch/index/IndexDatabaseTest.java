package zearch.index;

import org.junit.jupiter.api.Test;
import zearch.gram.Grammifier;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndexDatabaseTest {
    @Test
    void connectionTest() throws SQLException {
        Path path = Paths.get("tst/db/test-db");
        IndexDatabase.connect(path.toAbsolutePath().toString());
    }

    @Test
    void readWriteTest() throws SQLException {
        Path path = Paths.get("tst/db/test-db");
        IndexDatabase.connect(path.toAbsolutePath().toString());

        String url = "dbtest.zawie.io"; // fake url
        String content = "hello word";
        Map<String, Integer> gramScoreWrote = Grammifier.computeGramScore(content);

        System.out.println(gramScoreWrote);
        System.out.println("Writing");
        IndexDatabase.write(url, gramScoreWrote);

        System.out.println("Reading");
        Map<String,Integer> gramScoreRead = IndexDatabase.read(url);
        assertEquals(gramScoreWrote, gramScoreRead);

        IndexDatabase.close();
    }
}