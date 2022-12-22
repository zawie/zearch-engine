package zearch.index;

public class URLScorePair {

    private String url;
    private Integer score;

    public URLScorePair(String url, Integer score) {
        this.url = url;
        this.score = score;
    }
    public String getURL() {
        return url;
    }
    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return this.url + " ("+this.score+")";
    }
}
