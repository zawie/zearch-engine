package zearch.engine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class SearchResult {
    private String query;
    private List<Map<String, String>> sites;

    private String JSONstring;

    public SearchResult(String query, List<Map<String, String>> sites) {
        this.query = query;
        this.sites = sites;

        try {
            JSONObject result = new JSONObject();
            result.put("query", query);
            JSONArray list = new JSONArray();
            for (Map<String, String> data : sites) {
                JSONObject site = new JSONObject();
                site.put("url", data.getOrDefault("url", ""));
                site.put("title", data.getOrDefault("title", ""));
                site.put("description", data.getOrDefault("description", ""));
                site.put("keywords", data.getOrDefault("keywords", ""));
                site.put("author", data.getOrDefault("author", ""));
                list.put(site);
            }
            result.put("count", sites.size());
            result.put("results", list);
            this.JSONstring = result.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJSON() {
        return JSONstring;
    };

    public void print(int maxAmount) {
        int count = 0;
        for (Map<String, String> data : sites) {
            if (++count > maxAmount)
                break;
            System.out.println(data.getOrDefault("url","No URL")+"\t\t\t\t\t"+data.getOrDefault("title", "No Title"));
        }
    }

}
