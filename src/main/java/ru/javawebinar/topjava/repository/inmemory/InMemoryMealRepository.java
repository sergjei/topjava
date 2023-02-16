package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUserId(SecurityUtil.authUserId());
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        if (repository.containsKey(meal.getId()) && repository.get(meal.getId()).getUserId() == meal.getUserId()) {
            return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(int userId, int id) {
        if (repository.containsKey(id) && repository.get(id).getUserId() == userId) {
            return repository.remove(id) != null;
        } else {
            return false;
        }
    }

    @Override
    public Meal get(int userId, int id) {
        if (repository.containsKey(id) && repository.get(id).getUserId() == userId) {
            return repository.get(id);
        } else {
            return null;
        }
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getAllByPredicate(meal -> meal.getUserId() == userId);
    }

    public List<Meal> getAllFiltered(int userId, LocalDateTime startDate, LocalDateTime endDate) {
        return getAllByPredicate(meal -> meal.getUserId() == userId &&
                DateTimeUtil.isBetweenHalfOpen(meal.getDateTime(), startDate, endDate.plusDays(1)));
    }

    public List<Meal> getAllByPredicate(Predicate<Meal> filter) {
        return repository.values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

