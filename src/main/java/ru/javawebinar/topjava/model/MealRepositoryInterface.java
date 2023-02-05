package ru.javawebinar.topjava.model;

import java.time.LocalDateTime;

public interface MealRepositoryInterface {
    void createMeal(LocalDateTime dateTime, String description, int calories);

    void updateMeal(Integer id);

    void deleteMeal(Integer id);

    void readMeal(Integer id);

}
