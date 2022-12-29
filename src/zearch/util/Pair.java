package zearch.util;

public class Pair<F, S> {
    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    private final F first;
    private final S second;


    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }


}
