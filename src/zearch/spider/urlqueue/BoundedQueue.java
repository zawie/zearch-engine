package zearch.spider.urlqueue;

import java.util.Deque;
import java.util.LinkedList;

public class BoundedQueue<T> implements IQueue<T> {

    private int maxCapacity;
    private int usage = 0;
    private LinkedList<T> list;

    public BoundedQueue(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.list = new LinkedList<>();
    }
    @Override
    public void push(T element) {
        if (usage < maxCapacity) {
            usage++;
            list.push(element);
        }
    }

    @Override
    public T pop() {
        if (usage > 0) {
            usage--;
            return list.pop();
        }
        return null;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getUsage() {
        return usage;
    }
}
