package ru.javawebinar.topjava.repository.datajpa;

import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Repository

public class DataJpaMealRepository implements MealRepository {
    @PersistenceContext
    private EntityManager em;
    private final CrudMealRepository crudRepository;

    public DataJpaMealRepository(CrudMealRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        meal.setUser(em.getReference(User.class, userId));
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
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.getAllForPeriod(userId,startDateTime,endDateTime);
    }
}
