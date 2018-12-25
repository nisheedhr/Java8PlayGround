package com.nisheedh;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;

public class JavaStreamsTests {

    private List<Player> players;

    @Before
    public void setup() {
        players = IntStream.rangeClosed(1, 20).boxed().map(i ->
                new Player(Integer.toString(i), (i), Player.Country.values()[i % 4]))
                .collect(JavaStreamsTests.customListCollector());
    }

    @Test
    public void testGroupByCountry() {
        Map<Player.Country, List<Player>> countryListMap = players.stream().collect(Collectors.groupingBy(Player::getCountry));
        assertThat(countryListMap, IsMapContaining.hasKey(Player.Country.BRAZIL));
        assertThat(countryListMap, IsMapContaining.hasKey(Player.Country.USA));
        assertThat(countryListMap, IsMapContaining.hasKey(Player.Country.CHINA));
        assertThat(countryListMap, IsMapContaining.hasKey(Player.Country.INDIA));
    }

    public static <T> Collector<T, List<T>, List<T>> customListCollector() {

        Collector<T, List<T>, List<T>> result =  Collector.of(ArrayList::new, /* Supplier<T> provides an empty accumulator object */
                List::add, // Accumulator, adds up element into result container
                JavaStreamsTests.combiner(), // Combiner, used for parallel streams.  Combines two accumulator objects
                Function.identity(), // Optional finisher to be invoked at the end of accumulation process to transform to final result object
                IDENTITY_FINISH, CONCURRENT);
        return result;
    }

    public static <T> BinaryOperator<List<T>> combiner() {
        return (l1, l2) -> // Combiner, used for parallel streams.  Combines two accumulator objects
        {
            l1.addAll(l2);
            return l1;
        };
    }

    @Test
    public void testGroupScoresByCountry() {
        Map<Player.Country, Integer> summaryStatisticsMap = players.stream()
                .collect(Collectors.groupingBy(Player::getCountry, Collectors.summingInt(Player::getScore)));
        assertThat(summaryStatisticsMap, hasEntry(Player.Country.BRAZIL, 55));
        assertThat(summaryStatisticsMap, hasEntry(Player.Country.USA, 60));
        assertThat(summaryStatisticsMap, hasEntry(Player.Country.CHINA, 50));
        assertThat(summaryStatisticsMap, hasEntry(Player.Country.INDIA, 45));
    }

    @Test
    public void testMultiLevelGrouping() {
        // Group by country and for each country further group players by even and odd scores.
        Map<Player.Country, Map<Boolean, Integer>> countryScoreTypeMap = players.stream()
                .collect(Collectors.groupingBy(Player::getCountry, Collectors.groupingBy(player -> player.getScore() % 2 == 0,
                        Collectors.summingInt(p -> 1))));
        assertThat(countryScoreTypeMap.get(Player.Country.BRAZIL), hasEntry(false, 5));
        assertThat(countryScoreTypeMap.get(Player.Country.USA), hasEntry(true, 5));
        assertThat(countryScoreTypeMap.get(Player.Country.CHINA), hasEntry(true, 5));
        assertThat(countryScoreTypeMap.get(Player.Country.INDIA), hasEntry(false, 5));
    }
}
