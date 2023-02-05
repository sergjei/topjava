package ru.javawebinar.topjava.model;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MealsRepository implements MealRepositoryInterface {
    private volatile static List<Meal> meals=null;
    private final AtomicInteger ID_HOLDER = new AtomicInteger(6);
    public static List<Meal> getMealsRepository() {
        if(meals==null){
                meals = Arrays.asList(
                new Meal(0,LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(1,LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(2,LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(3,LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(4,LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(5,LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(6,LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        }
        return meals;
    }

    @Override
    public void createMeal(LocalDateTime dateTime, String description, int calories) {
        Integer id = ID_HOLDER.incrementAndGet();
        meals.add(new Meal(id,dateTime,description,calories));
    }

    @Override
    public void updateMeal(Integer id) {

    }

    @Override
    public void deleteMeal(Integer id) {
        meals = meals.stream().filter((meal)-> !Objects.equals(meal.getId(), id)).collect(Collectors.toList());
    }

    @Override
    public void readMeal(Integer id) {

    }
}
