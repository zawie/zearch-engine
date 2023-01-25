package zearch.util;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class TopKCollector<T> implements Collector<T, TreeSet<T>, List<T>> {

    private final int k;
    private final Comparator<T> comparator;

    public TopKCollector(int k, Comparator<T> comparator) {
        this.k = k;
        this.comparator = comparator;
    }


    @Override
    public Supplier<TreeSet<T>> supplier() {
        return () -> new TreeSet<>(comparator);
    }

    @Override
    public BiConsumer<TreeSet<T>, T> accumulator() {
        return (container, element) -> {
            container.add(element);
            if (container.size() > k)
                container.pollFirst();
        };
    }

    @Override
    public BinaryOperator<TreeSet<T>> combiner() {
        return (container0, container1) -> {
            container0.addAll(container1);
            while(container0.size() > k)
                container0.pollFirst();
            return container0;
        };
    }

    @Override
    public Function<TreeSet<T>, List<T>> finisher() {
        return (container) -> {
            List<T> list = container.stream().collect(Collectors.toList());
            Collections.reverse(list);
            return list;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }
}
