package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int userId) {
        return repository.save(new Meal(null, meal.getDateTime(), meal.getDescription(),
                meal.getCalories()), userId);
    }

    public void delete(int userId, int id) {
        checkNotFoundWithId(repository.delete(userId, id), id);
    }

    public Meal get(int userId, int id) {
        return checkNotFoundWithId(repository.get(userId, id), id);
    }

    public List<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    public List<Meal> getAllFiltered(int userId, LocalDateTime startDate, LocalDateTime endDate) {
        return repository.getAllFiltered(userId, startDate, endDate);
    }

    public void update(Meal meal, int userId) {
        checkNotFoundWithId(repository.save(new Meal(meal.getId(), null, meal.getDateTime(),
                meal.getDescription(), meal.getCalories()), userId), meal.getId());
    }
}