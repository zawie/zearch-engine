package zearch.index;

import zearch.gram.GramData;
import zearch.gram.Grammifier;

import java.sql.*;
import java.util.*;

public class IndexDatabase {

    private static Collection<String> indexGrams = GramData.SINGLETON.getGrams();
    private static Connection connection;

    public static void close() throws SQLException {
        connection.close();
    }

    public static void connect(String dbFilepath) throws SQLException {
        // NOTE: This is an embedded database, so the password need not be secret.
        connect(dbFilepath, "admin", "password");
    }

    public static void connect(String dbFilepath, String dbUser, String dbPassword) throws SQLException {
        String jdbcURL = "jdbc:h2:"+dbFilepath;

        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

        System.out.println("Connected to H2 embedded database.");

        Statement statement = connection.createStatement();

        StringBuilder gramTableColumns = new StringBuilder("`link` VARCHAR(2048) PRIMARY KEY");
        gramTableColumns.append(", Timestamp DATETIME NOT NULL DEFAULT(CURRENT_TIMESTAMP)");
        for (String gram: indexGrams) {
            gramTableColumns.append(", `"+gram+"` TINYINT"); //UNSIGNED SPARSE
        }
        statement.execute("CREATE TABLE IF NOT EXISTS text_gram_table ("+gramTableColumns+");");
        connection.commit(); // now the database physically exists
    }
    public static void write(String url, Map<String, Integer> gramToCount) throws SQLException {
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

    public static Map<String, Integer> read(String url) throws SQLException {
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

    public static List<URLScorePair> best(Map<String, Integer> gramToCount, Integer amount) throws SQLException {

        StringBuilder expression = new StringBuilder();
        for (Map.Entry entry : gramToCount.entrySet()) {
            String col = "`"+entry.getKey()+"`";
            expression.append("+ "+entry.getValue()+"*NVL2("+col+", "+col+", 0)");
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT TOP "+amount+" `link`, "+expression+" as Score FROM text_gram_table GROUP BY `link` ORDER BY Score DESC;");

        List<URLScorePair> output = new ArrayList<>(1000);
        while (resultSet.next()) {
            String url = resultSet.getString(1);
            Integer score = Integer.parseInt(resultSet.getString(2));
            output.add(new URLScorePair(url, score));
        }
        return output;
    }

    public static int getRowCount() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT count(*) from text_gram_table");
        //Retrieving the result
        rs.next();
        return rs.getInt(1);
    }

}
