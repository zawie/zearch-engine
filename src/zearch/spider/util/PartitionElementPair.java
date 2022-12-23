package zearch.spider.util;

public class PartitionElementPair<P,E> {
    private P partition;
    private E element;

    public PartitionElementPair(P partition, E element) {
        this.partition = partition;
        this.element = element;
    }

    public P getPartition() {
        return partition;
    }

    public E getElement() {
        return element;
    }

}
