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
        columns.append("`id` BIGINT AUTO_INCREMENT PRIMARY KEY");
        columns.append(", ").append("`url` VARCHAR(2048)");
        columns.append(", ").append("`timestamp` DATETIME NOT NULL DEFAULT(CURRENT_TIMESTAMP)");
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

        columns.add("url");
        values.add("'"+url+"'");
        columns.add("timestamp");
        values.add("CURRENT_TIMESTAMP");
        columns.add("title");
        values.add("'" + boundString(metaData.getOrDefault("title",""), 128).replaceAll("'", "''")          + "'");
        columns.add("description");
        values.add("'" + boundString(metaData.getOrDefault("description",""), 1024).replaceAll("'", "''")        + "'");
        columns.add("keywords");
        values.add("'" + boundString(metaData.getOrDefault("keywords",""), 256).replaceAll("'", "''")           + "'");
        columns.add("author");
        values.add("'" + boundString(metaData.getOrDefault("author",""), 64).replaceAll("'", "''")             + "'");

        assert(hashes.length == MinHasher.COUNT);
        for (int h = 0; h < MinHasher.COUNT; h++) {
            String hex = Integer.toHexString(hashes[h]);
            while (hex.length() < 2) hex = "0" + hex;
            columns.add("`hash"+h+"`");
            values.add("X'" + hex + "'");
        }
//        statement.execute("DELETE FROM index_table WHERE `url` = '"+url+"';"); //TODO: Remove duplicate entries periodically
        statement.execute("INSERT INTO index_table ("+String.join(", ", columns)+") VALUES (" +String.join(", ", values)+ ")");
    }

    public static Iterator<IndexEntry> getAllIndexEntries() {


        Statement stmt = null;
        ResultSet srs = null;
        try {
            stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, //or ResultSet.TYPE_FORWARD_ONLY
                    ResultSet.CONCUR_READ_ONLY);
            srs = stmt.executeQuery("SELECT * FROM index_table");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet finalSrs = srs;
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                boolean nextExists;
                try {
                    nextExists = finalSrs.next();
                    finalSrs.previous();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return nextExists;
            }

            @Override
            public IndexEntry next() {
                try {
                    if (!finalSrs.next())
                        return null;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                long id;
                try {
                    id = finalSrs.getLong("id");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                int[] hashes = new int[MinHasher.COUNT];
                for (int h = 0; h < MinHasher.COUNT; h++) {
                    try {
                       byte b = finalSrs.getByte("hash" + h);
                       hashes[h] = Byte.toUnsignedInt(b);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                return new IndexEntry(id, hashes);
            }
        };
    }

    public static Map<String, String> getMetaData(Long rowId) throws SQLException {
        Statement statement = connection.createStatement();
        List<String> columns = new LinkedList<>();
        columns.add("url");
        columns.add("title");
        columns.add("description");
        columns.add("keywords");
        columns.add("author");
        ResultSet rs = statement.executeQuery("SELECT ("+String.join(", ", columns)+") FROM index_table WHERE url = "+rowId);
        //Retrieving the result
        rs.next();
        Map<String, String> metaData = new HashMap<>();
        for (String col: columns) {
            metaData.put(col, rs.getString(col));
        }
        return metaData;
    }
}
