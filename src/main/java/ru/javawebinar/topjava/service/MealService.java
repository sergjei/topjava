package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int userId) {
        return repository.save(new Meal(userId, meal.getDateTime(), meal.getDescription(), meal.getCalories()));
    }

    public void delete(int userId, int id) {
        checkNotFoundWithId(repository.delete(userId, id), id);
    }

    public Meal get(int userId, int id) {
        return checkNotFoundWithId(repository.get(userId, id), id);
    }

    public Collection<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    public Collection<Meal> getAllFiltered(int userId, String startDate, String endDate) {
        LocalDateTime startDateF = !startDate.isEmpty() ? LocalDateTime.parse((startDate + "T00:00:00")) : LocalDateTime.MIN;
        LocalDateTime endDateF = !endDate.isEmpty() ? LocalDateTime.parse((endDate + "T00:00:00")) : LocalDateTime.MAX.minusDays(1);
        return repository.getAllFiltered(userId, startDateF, endDateF);
    }

    public void update(Meal meal, int userId) {
        checkNotFoundWithId(repository.save(new Meal(meal.getId(), userId, meal.getDateTime(), meal.getDescription(), meal.getCalories())), meal.getId());
    }
}