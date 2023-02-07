package ru.javawebinar.topjava.model;

import java.util.List;

public interface MealRepositoryInterface {
    void create(Meal meal);

    void update(Integer id, Meal meal);

    void delete(Integer id);

    List<Meal> readAll();

    Meal readById(Integer id);

}
