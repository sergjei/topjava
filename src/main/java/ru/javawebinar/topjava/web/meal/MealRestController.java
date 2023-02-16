package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private MealService mealService;


    public List<MealTo> getAll() {
        log.info("getAll");
        int userId = SecurityUtil.authUserId();
        return MealsUtil.getTos(mealService.getAll(userId), SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getAllFiltered(LocalDateTime startDate, LocalDateTime endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getAllFiltered");
        int userId = SecurityUtil.authUserId();
        return MealsUtil.getFilteredTos(mealService.getAllFiltered(userId, startDate, endDate),
                SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);
    }

    public Meal get(int id) {
        log.info("get {}", id);
        int userId = SecurityUtil.authUserId();
        return mealService.get(userId, id);
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        int userId = SecurityUtil.authUserId();
        checkNew(meal);
        return mealService.create(meal, userId);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        int userId = SecurityUtil.authUserId();
        mealService.delete(userId, id);
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        int userId = SecurityUtil.authUserId();
        mealService.update(meal, userId);
    }
}