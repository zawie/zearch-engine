package zearch.engine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import zearch.util.Pair;

import java.util.List;
import java.util.Map;

public class SearchResult {
    private String query;
    private List<Pair<Map<String, String>, Double>> topResults;

    private String JSONstring;

    public SearchResult(String query, List<Pair<Map<String, String>, Double>> topResults) {
        this.query = query;
        this.topResults = topResults;

        try {
            JSONObject result = new JSONObject();
            result.put("query", query);
            JSONArray list = new JSONArray();
            for (Pair<Map<String, String>, Double> pair : topResults) {
                Map<String, String> data = pair.getFirst();
                Double score = pair.getSecond();
                JSONObject site = new JSONObject();
                site.put("url", data.getOrDefault("url", ""));
                site.put("title", data.getOrDefault("title", ""));
                site.put("description", data.getOrDefault("description", ""));
                site.put("keywords", data.getOrDefault("keywords", ""));
                site.put("author", data.getOrDefault("author", ""));
                site.put("score", score);
                list.put(site);
            }
            result.put("count", topResults.size());
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
        for (Pair<Map<String, String>, Double> pair : topResults) {
            Map<String, String> data = pair.getFirst();
            Double score = pair.getSecond();
            if (++count > maxAmount)
                break;
            System.out.println("["+score+"] "+data.getOrDefault("url","No URL")+"\t\t\t\t\t"+data.getOrDefault("title", "No Title"));
        }
    }

}
