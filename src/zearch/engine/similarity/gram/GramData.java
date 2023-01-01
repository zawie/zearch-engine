package zearch.engine.similarity.gram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GramData {

    public static GramData SINGLETON;

    public static final int MAX_COUNT = 77534223;
    public static final int MIN_COUNT = 1;

    private static long TRI_COUNT;

    static {
        try {
            SINGLETON = new GramData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private  Map<String, Integer> gramToCount;
    private GramData() throws IOException {
        gramToCount = new HashMap<>();

        this.TRI_COUNT = 0l;
        BufferedReader reader = new BufferedReader(new FileReader("./data/trigrams.txt"));
        String line = reader.readLine();
        while (line != null) {
            String[] split = line.split(" ");
            int count = Integer.parseInt(split[1]);
            TRI_COUNT += count;
            gramToCount.put(split[0].toLowerCase(), count);
            line = reader.readLine();
        }
        reader.close();
    }

    public Collection<String> getGrams() {
        return gramToCount.keySet();
    }

    public int getCount(String gram) {
        if (!gramToCount.containsKey(gram))
            return MIN_COUNT;
        return gramToCount.get(gram);
    }

    public long getNumTris() {
        return TRI_COUNT;
    }
}