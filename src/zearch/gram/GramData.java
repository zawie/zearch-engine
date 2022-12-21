package zearch.gram;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GramData {

    public static GramData SINGLETON;

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

        BufferedReader reader = new BufferedReader(new FileReader("./data/trigrams.txt"));
        String line = reader.readLine();
        while (line != null) {
            String[] split = line.split(" ");
            gramToCount.put(split[0].toLowerCase(), Integer.parseInt(split[1]));
            line = reader.readLine();
        }
        reader.close();
    }

    public Collection<String> getGrams() {
        return gramToCount.keySet();
    }

    public int getCount(String gram) {
        if (!gramToCount.containsKey(gram))
            return 0;
        return gramToCount.get(gram);
    }

}
