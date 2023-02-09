package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealRepository {
    Meal create(Meal meal);

    Meal update(Meal meal);

    void delete(int id);

    List<Meal> readAll();

    Meal readById(int id);

}
