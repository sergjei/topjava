package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int NOT_FOUND = 13;
    public static Meal admeal10 = new Meal(START_SEQ + 12, LocalDateTime.of(2023, 2, 19, 10, 0), "Breakfast", 600);
    public static Meal admeal9 = new Meal(START_SEQ + 11, LocalDateTime.of(2023, 2, 19, 13, 0), "Lunch", 300);
    public static Meal admeal8 = new Meal(START_SEQ + 10, LocalDateTime.of(2023, 2, 19, 19, 0), "Supper", 600);
    public static Meal umeal7 = new Meal(START_SEQ + 9, LocalDateTime.of(2023, 2, 20, 20, 0), "supper", 500);
    public static Meal umeal6 = new Meal(START_SEQ + 8, LocalDateTime.of(2023, 2, 20, 14, 0), "Lunch", 500);
    public static Meal umeal5 = new Meal(START_SEQ + 7, LocalDateTime.of(2023, 2, 20, 10, 0), "Breakfast", 600);
    public static Meal umeal4 = new Meal(START_SEQ + 6, LocalDateTime.of(2023, 2, 20, 0, 0), "Cheatmeal", 800);
    public static Meal umeal3 = new Meal(START_SEQ + 5, LocalDateTime.of(2023, 2, 19, 19, 0), "Supper", 600);
    public static Meal umeal2 = new Meal(START_SEQ + 4, LocalDateTime.of(2023, 2, 19, 13, 0), "Lunch", 300);
    public static Meal umeal1 = new Meal(START_SEQ + 3, LocalDateTime.of(2023, 2, 19, 10, 0), "Breakfast", 600);
    public static final List<Meal> mealsUser = Stream.of(umeal1, umeal2, umeal3, umeal4, umeal5, umeal6, umeal7)
            .sorted((m1, m2) -> m1.getDateTime().isBefore(m2.getDateTime()) ? 1 : -1).collect(Collectors.toList());
    public static final List<Meal> mealsUserOn19 = Stream.of(umeal1, umeal2, umeal3)
            .sorted((m1, m2) -> m1.getDateTime().isBefore(m2.getDateTime()) ? 1 : -1).collect(Collectors.toList());


    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2023, 02, 21, 10, 00), "Breakfast", 100);
    }

    public static Meal getUpdated() {
        Meal meal = new Meal(umeal1);
        meal.setDateTime(LocalDateTime.of(2023, 02, 21, 11, 11));
        meal.setDescription("updated");
        meal.setCalories(111);
        return meal;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }
}



