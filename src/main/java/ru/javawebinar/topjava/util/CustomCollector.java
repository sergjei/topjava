package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collector.Characteristics.UNORDERED;

public class CustomCollector implements
        Collector<List<UserMeal>, Map<List<UserMeal>, Integer>, List<UserMealWithExcess>> {
    private int caloriesPerDay;
    private LocalTime startTime;
    private LocalTime endTime;

    @Override
    public Supplier<Map<List<UserMeal>, Integer>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<List<UserMeal>, Integer>, List<UserMeal>> accumulator() {

        return (container, element) ->
                container.put(element, element.stream()
                        .map(UserMeal::getCalories)
                        .reduce(0, Integer::sum));

    }

    @Override
    public BinaryOperator<Map<List<UserMeal>, Integer>> combiner() {
        return (c1, c2) -> {
            c2.forEach((key, value) -> c1.merge(key, value, (e1, e2) -> e1.equals(e2) ? e1 : 0));
            return c1;
        };
    }

    @Override

    public Function<Map<List<UserMeal>, Integer>, List<UserMealWithExcess>> finisher() {

        return (container) -> {
            List<UserMealWithExcess> result = new ArrayList<>();
            container.entrySet().forEach(
                    e -> {
                        boolean isExcess = e.getValue() > caloriesPerDay;
                        result.addAll(e.getKey().stream()
                                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                                .map(userMeal -> new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), isExcess))
                                .collect(Collectors.toCollection(ArrayList::new)));
                    });
            return result;
        };

    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.singleton(UNORDERED);
    }

    public static CustomCollector getCustomCollector(Integer caloriesPerDay, LocalTime startTime, LocalTime endTime) {
        CustomCollector customCollector = new CustomCollector();
        customCollector.caloriesPerDay = caloriesPerDay;
        customCollector.startTime = startTime;
        customCollector.endTime = endTime;
        return customCollector;
    }
}

