package zearch.spider.util;

import java.util.*;

public class RoundPartition<P,E> {

    private Queue<P> partitionQueue;
    private Map<P, Queue<E>> partitions;

    private int elementCount;
    private int partitionCount;

    private int maxPartitionSize;
    private int maxPartitionCount;
    public RoundPartition(int maxPartitionSize, int maxPartitionCount) {
        partitions = new HashMap<>();
        partitionQueue = new LinkedList<>();
        this.maxPartitionSize = maxPartitionSize;
        this.maxPartitionCount = maxPartitionCount;
        elementCount = 0;
        partitionCount = 0;
    }

    public boolean addTo(P partition, E element) {
        boolean exists = partitions.containsKey(partition);
        partitions.putIfAbsent(partition, new LinkedList<>());
        Queue<E> elements = partitions.get(partition);

        if (elements.size() >= maxPartitionSize)
            return false;
        if (exists && partitionCount >= maxPartitionCount)
            return false;

        elements.add(element);
        elementCount++;
        if (!exists) {
            partitionQueue.add(partition);
            partitionCount++;
        }
        return true;

    }

    public E pullNextPartition() {
        if (partitionQueue.isEmpty())
            return null;
        P partition = partitionQueue.remove();
        Queue<E> elements = partitions.get(partition);
        E element = elements.remove();
        elementCount--;
        if (!elements.isEmpty()) {
            partitionQueue.add(partition);
        } else {
            partitionCount--;
        }
        return element;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public int getElementCount() {
        return elementCount;
    }
}
