package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MealTestData {
    public static final int NOT_FOUND = 13;
    public static List<Meal> mealsUser = Arrays.asList(
            new Meal(100009, LocalDateTime.parse("2023-02-20T20:00"), "supper", 500),
            new Meal(100008, LocalDateTime.parse("2023-02-20T14:00"), "Lunch", 500),
            new Meal(100007, LocalDateTime.parse("2023-02-20T10:00"), "Breakfast", 600),
            new Meal(100006, LocalDateTime.parse("2023-02-19T22:00"), "Cheatmeal", 800),
            new Meal(100005, LocalDateTime.parse("2023-02-19T19:00"), "Supper", 600),
            new Meal(100004, LocalDateTime.parse("2023-02-19T13:00"), "Lunch", 300),
            new Meal(100003, LocalDateTime.parse("2023-02-19T10:00"), "Breakfast", 600));
    public static List<Meal> mealsAdmin = Arrays.asList(
            new Meal(1000010, LocalDateTime.parse("2023-02-19T10:00"), "Breakfast", 600),
            new Meal(1000011, LocalDateTime.parse("2023-02-19T13:00"), "Lunch", 300),
            new Meal(1000012, LocalDateTime.parse("2023-02-19T19:00"), "Supper", 600));

    public static Meal getNew() {
        return new Meal(LocalDateTime.parse("2023-02-21T10:00"), "Breakfast", 100);
    }

    public static Meal getUpdated() {
        Meal meal = new Meal(mealsUser.get(4));
        meal.setDateTime(LocalDateTime.parse("2023-02-21T11:11"));
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



