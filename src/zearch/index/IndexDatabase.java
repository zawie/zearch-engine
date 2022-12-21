package zearch.index;

import zearch.gram.GramData;

import java.sql.*;
import java.util.*;

public class IndexDatabase {
    private  Connection connection;

    private static Collection<String> indexGrams = GramData.SINGLETON.getGrams();
    public IndexDatabase() throws SQLException, Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

        String jdbcURL = "jdbc:derby:~/Developer/db/zearch-index-db;create=true"; //TODO: Abstract out

        this.connection = DriverManager.getConnection(jdbcURL, "admin", "password");

        System.out.println("Connected to Derby embedded database.");

        Statement statement = connection.createStatement();

        StringBuilder gramTableColumns = new StringBuilder("\"link\" VARCHAR(2048) PRIMARY KEY");
        gramTableColumns.append(", Timestamp DATETIME NOT NULL DEFAULT(CURRENT_TIMESTAMP)");
        for (String gram: indexGrams) {
            gramTableColumns.append(", \""+gram+"\" TINYINT"); //UNSIGNED SPARSE
        }
        statement.execute("CREATE TABLE text_gram_table ( "+gramTableColumns+" );");
        connection.commit(); // now the database physically exists
    }

    public void write(String url, Map<String, Integer> gramToCount) throws SQLException {
        Statement statement = connection.createStatement();

        List<String> columns = new LinkedList<>();
        List<String> values = new LinkedList<>();

        columns.add(("`link`"));
        values.add("'"+url+"'");

        columns.add(("`Timestamp`"));
        values.add("CURRENT_TIMESTAMP");

        for (Map.Entry<String,Integer> entry : gramToCount.entrySet()) {
            if (indexGrams.contains(entry.getKey())) {
                columns.add("`" + entry.getKey() + "`");
                values.add(entry.getValue().toString());
            }
        }

        statement.execute("DELETE FROM text_gram_table WHERE `link` = '"+url+"';");
        statement.execute("INSERT INTO text_gram_table " +
                "(" +String.join(", ", columns)+ ")" +
                " VALUES ("+ String.join(", ", values)+");");
    }

    public Map<String, Integer> read(String url) throws SQLException {
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM text_gram_table WHERE link = '"+url+"';");

        rs.next();
        Map<String, Integer> gramToCount = new HashMap<>();

        for (String gram: indexGrams) {
            int v = rs.getInt(gram);
            if (v > 0)
                gramToCount.put(gram, v);
        }

        return gramToCount;
    }

}
