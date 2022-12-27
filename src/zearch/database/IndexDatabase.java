package zearch.database;

import zearch.minhash.MinHasher;

import java.net.URL;
import java.sql.*;
import java.util.*;

public class IndexDatabase {

    private static Connection connection;

    public static void close() throws SQLException {
        connection.close();
    }

    public static void connect(String dbFilepath) throws SQLException {
        String jdbcURL = "jdbc:h2:"+dbFilepath;

        connection = DriverManager.getConnection(jdbcURL, "admin", "password");

        System.out.println("Connected to H2 embedded database.");

        Statement statement = connection.createStatement();

        StringBuilder columns = new StringBuilder();
        columns.append("`url` VARCHAR(2048) PRIMARY KEY");
        columns.append(", ").append("Timestamp DATETIME NOT NULL DEFAULT(CURRENT_TIMESTAMP)");
        columns.append(", ").append("`title` VARCHAR(128)");
        columns.append(", ").append("`description` VARCHAR(1024)");
        columns.append(", ").append("`keywords` VARCHAR(256)");
        columns.append(", ").append("`author` VARCHAR(64)");
        for (Integer h = 0; h < MinHasher.COUNT; h++) {
            columns.append(", ").append("`hash"+h+"` BINARY(1)");
        }
        statement.execute("CREATE TABLE IF NOT EXISTS index_table ("+columns+");");
        connection.commit(); // now the database physically exists
    }

    public static int getRowCount() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT count(*) from index_table");
        //Retrieving the result
        rs.next();
        return rs.getInt(1);
    }

    private static String boundString(String s, int maxSize) {
        if (s.length() > maxSize) {
            return s.substring(0, maxSize-1);
        }
        return s;
    }
    public static void write(URL url, Map<String, String> metaData, int[] hashes) throws SQLException {
        Statement statement = connection.createStatement();

        List<String> columns = new LinkedList<>();
        List<String> values = new LinkedList<>();

        values.add("'"+url+"'");
        values.add("CURRENT_TIMESTAMP");
        values.add("'" + boundString(metaData.getOrDefault("title",""), 128).replaceAll("'", "''")          + "'");
        values.add("'" + boundString(metaData.getOrDefault("description",""), 1024).replaceAll("'", "''")        + "'");
        values.add("'" + boundString(metaData.getOrDefault("keywords",""), 256).replaceAll("'", "''")           + "'");
        values.add("'" + boundString(metaData.getOrDefault("author",""), 64).replaceAll("'", "''")             + "'");

        assert(hashes.length == MinHasher.COUNT);
        for (int h = 0; h < MinHasher.COUNT; h++) {
            String hex = Integer.toHexString(hashes[h]);
            while (hex.length() < 2) hex = "0" + hex;
            values.add("X'" + hex + "'");
        }
        statement.execute("DELETE FROM index_table WHERE `url` = '"+url+"';");
        statement.execute("INSERT INTO index_table VALUES (" +String.join(", ", values)+ ")");
    }
}
