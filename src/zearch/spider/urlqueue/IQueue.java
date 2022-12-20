package zearch.spider.urlqueue;

public interface IQueue<T> {

    void push(T element);
    T pop();
}
