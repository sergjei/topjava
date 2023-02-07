package ru.javawebinar.topjava.model;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class MealsMemoryCRUDRepository implements MealRepositoryInterface {
    private volatile static ConcurrentHashMap<Integer, Meal> meals = null;
    private final AtomicInteger ID_HOLDER = new AtomicInteger(6);

    public MealsMemoryCRUDRepository() {
        super();
        fullfillRepository();
    }

    public static void fullfillRepository() {
        if (meals == null) {
            meals = new ConcurrentHashMap<>();
            meals.put(0, new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
            meals.put(1, new Meal(1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
            meals.put(2, new Meal(2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
            meals.put(3, new Meal(3, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
            meals.put(4, new Meal(4, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
            meals.put(5, new Meal(5, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
            meals.put(6, new Meal(6, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
        }
    }

    @Override
    public void create(Meal meal) {
        Integer id = ID_HOLDER.incrementAndGet();
        meals.put(id, new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories()));
    }

    @Override
    public void update(Integer id, Meal meal) {
        Meal oldMeal = meals.get(id);
        LocalDateTime newDateTime = meal.getDateTime() != null ? meal.getDateTime() : oldMeal.getDateTime();
        String newDescription = meal.getDescription() != null ? meal.getDescription() : oldMeal.getDescription();
        int newCalories = (meal.getCalories() != -1) ? meal.getCalories() : oldMeal.getCalories();
        meals.put(id, new Meal(id, newDateTime, newDescription, newCalories));
    }

    @Override
    public void delete(Integer id) {
        meals.remove(id);
    }

    @Override
    public Meal readById(Integer id) {
        return meals.get(id);
    }

    @Override
    public List<Meal> readAll() {
        return new ArrayList<>(meals.values());
    }
}