package zearch.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;

public class IndexRowEntry {

    private int[] hashes;
    private  Map<String, String> metaData;

    private URL url;
    public IndexRowEntry(URL url, int[] hashes, Map<String, String> metaData) {
        this.url = url;
        this.hashes = Arrays.copyOf(hashes, hashes.length);
        this.metaData = metaData;
    }

    public int[] getHashes() {
        return hashes;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    };

    public URL getURL() {
        return url;
    }
    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", metaData.getOrDefault("title", ""));
        jsonObject.put("description", metaData.getOrDefault("description", ""));
        jsonObject.put("keywords", metaData.getOrDefault("keywords", ""));
        jsonObject.put("author", metaData.getOrDefault("author", ""));
        jsonObject.put("url", url.toString());
        jsonObject.put("hashes", new JSONArray(this.hashes));
        return jsonObject.toString();
    };

}
