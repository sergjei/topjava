package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Repository
@Transactional(readOnly = true)
public class DataJpaMealRepository implements MealRepository {

    private final CrudUserRepository userRepository;
    private final CrudMealRepository crudRepository;

    public DataJpaMealRepository(CrudMealRepository crudRepository, CrudUserRepository userRepository) {
        this.crudRepository = crudRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        meal.setUser(userRepository.getReferenceById(userId));
        meal = (!meal.isNew()) && (get(meal.id(), userId) == null) ? null : meal;
        if (meal == null) {
            return null;
        } else crudRepository.save(meal);
        return meal;
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        if (get(id, userId) == null) {
            return false;
        } else return crudRepository.delete(id) != 0;
    }

    @Override
    @Transactional
    public Meal get(int id, int userId) {
        Meal searchedMeal = crudRepository.findById(id).orElse(null);
        if (searchedMeal != null && userId == searchedMeal.getUser().id()) {
            return searchedMeal;
        } else {
            return null;
        }
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.getAll(userId);
    }

    @Override
    public <T> List<Meal> getBetweenHalfOpen(T startDateTime, T endDateTime, int userId) {
        return crudRepository.getAllForPeriod(userId, (LocalDateTime) startDateTime, (LocalDateTime) endDateTime);
    }

    @Override
    @Transactional
    public Meal getWithUser(int id, int userId) {
        return crudRepository.getWithUser(id, userId);
    }
}
