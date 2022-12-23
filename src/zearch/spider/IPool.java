package zearch.spider;

import java.net.MalformedURLException;

public interface IPool<T> {

    void push(T element);
    T pull() throws MalformedURLException, InterruptedException;
}
