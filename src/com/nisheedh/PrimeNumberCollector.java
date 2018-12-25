package com.nisheedh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class PrimeNumberCollector implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {
    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () ->  new HashMap<Boolean, List<Integer>>() {{
            put(true, new ArrayList<>());
            put(false, new ArrayList<>());
        }};
    }

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (acc, candidate) -> acc.get(isPrime(acc.get(true), candidate)).add(candidate);
    }

    private boolean isPrime(List<Integer> primeNumbers, Integer candidate) {
        int candidateRoot = (int) Math.sqrt(candidate);
        return optimisedFilter(primeNumbers, (i -> i <= candidateRoot))
                .stream()
                .noneMatch(p -> candidate % p == 0);
    }

    /**
     * Normal filter iterates over all the elements in a list. This filter stops filtering once
     * the condition doesn't match. It works on collections which are already ordered based on the
     * filtering condition.
     */
    public static <A> List<A> optimisedFilter(List<A> input, Predicate<A> predicate) {
        for (int i = 0; i < input.size(); ++i) {
            if (!predicate.test(input.get(i))) {
                return input.subList(0, i);
            }
        }
        return input;
    }

    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return (map1, map2) -> {
            map1.get(true).addAll(map2.get(true));
            map2.get(false).addAll(map2.get(false));
            return map1;
        };
    }

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
    }

}
