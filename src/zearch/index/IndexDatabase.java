package zearch.index;

import zearch.gram.GramData;
import zearch.gram.Grammifier;

import java.sql.*;
import java.util.*;

public class IndexDatabase {
    private  Connection connection;

    private static Collection<String> indexGrams = GramData.SINGLETON.getGrams();
    public IndexDatabase() throws SQLException {
        // NOTE: This is an embedded database, so the password need not be secret.
        String jdbcURL = "jdbc:h2:~/Developer/db/zearch-index-dev"; //TODO: Abstract out
        String username = "admin";
        String password = "password";

        this.connection = DriverManager.getConnection(jdbcURL, username, password);

        System.out.println("Connected to H2 embedded database.");

        Statement statement = connection.createStatement();

        StringBuilder gramTableColumns = new StringBuilder("`link` VARCHAR(2048) PRIMARY KEY");
        for (String gram: indexGrams) {
            gramTableColumns.append(", `"+gram+"` TINYINT"); //UNSIGNED SPARSE
        }
        statement.execute("CREATE TABLE text_gram_table ("+gramTableColumns+");");
        connection.commit(); // now the database physically exists

    }

    public void write(String url, Map<String, Short> gramToCount) throws SQLException {
        Statement statement = connection.createStatement();

        List<String> columns = new LinkedList<>();
        List<String> values = new LinkedList<>();

        columns.add(("`link`"));
        values.add("'"+url+"'");

        for (Map.Entry<String,Short> entry : gramToCount.entrySet()) {
            if (indexGrams.contains(entry.getKey())) {
                columns.add("`" + entry.getKey() + "`");
                values.add(entry.getValue().toString());
            }
        }

        statement.execute("INSERT INTO text_gram_table " +
                "(" +String.join(", ", columns)+ ")" +
                " VALUES ("+ String.join(", ", values)+");");
    }

    public Map<String, Short> read(String url) throws SQLException {
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM text_gram_table WHERE link = '"+url+"';");

        rs.next();
        Map<String, Short> gramToCount = new HashMap<>();

        for (String gram: indexGrams) {
            Short v = rs.getShort(gram);
            if (v > 0)
                gramToCount.put(gram, v);
        }

        return gramToCount;
    }

}
