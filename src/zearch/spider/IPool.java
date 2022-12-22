package zearch.spider;

public interface IPool<T> {

    void push(T element);
    T pull();
}
