package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class MemoryMealsRepository implements MealRepository {
    private static final Map<Integer, Meal> meals = new ConcurrentHashMap<>();
    private final AtomicInteger idHolder = new AtomicInteger(6);

    public MemoryMealsRepository() {
        fulfillRepository();
    }

    public void fulfillRepository() {
        if (meals.isEmpty()) {
            this.create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
            this.create(new Meal(1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
            this.create(new Meal(2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
            this.create(new Meal(3, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
            this.create(new Meal(4, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
            this.create(new Meal(5, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
            this.create(new Meal(6, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
        }
    }

    public Meal create(Meal meal) {
        Integer id = idHolder.incrementAndGet();
        Meal newMeal = new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories());
        meals.put(id, newMeal);
        return newMeal;
    }

    @Override
    public Meal update(Meal meal) {
        synchronized (meals){
        Meal oldMeal = meals.get(meal.getId());
        LocalDateTime newDateTime = meal.getDateTime() != null ? meal.getDateTime() : oldMeal.getDateTime();
        String newDescription = meal.getDescription() != null ? meal.getDescription() : oldMeal.getDescription();
        int newCalories = (meal.getCalories() != -1) ? meal.getCalories() : oldMeal.getCalories();
        Meal updatedMeal = new Meal(meal.getId(), newDateTime, newDescription, newCalories);
        meals.put(meal.getId(), updatedMeal);
        return updatedMeal;}
    }

    @Override
    public void delete(int id) {
        meals.remove(id);
    }

    @Override
    public Meal readById(int id) {
        return meals.get(id);
    }

    @Override
    public List<Meal> readAll() {
        return new ArrayList<>(meals.values());
    }
}