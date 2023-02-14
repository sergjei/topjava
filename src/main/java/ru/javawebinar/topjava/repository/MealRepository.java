package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;

// TODO add userId
public interface MealRepository {
    // null if updated meal does not belong to userId
    Meal save(Meal meal);

    // false if meal does not belong to userId
    boolean delete(int userId, int id);

    // null if meal does not belong to userId
    Meal get(int userId ,int id);

    // ORDERED dateTime desc
    Collection<Meal> getAll(int userId);

    Collection<Meal> getAllFiltered(int userId, LocalDateTime startDate, LocalDateTime endDate, LocalTime startTime, LocalTime endTime);
}
