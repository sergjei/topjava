package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))

public class MealServiceTest {
    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService mealService;

    @Test
    public void get() {
        int userId = UserTestData.USER_ID;
        int mealId = 100006;
        Meal meal = mealService.get(mealId, userId);
        assertMatch(meal, mealsUser.get(3));
    }

    @Test
    @Rollback
    public void delete() {
        int userId = UserTestData.USER_ID;
        int mealId = 100006;
        mealService.delete(mealId, userId);
        assertThrows(NotFoundException.class, () -> mealService.get(mealId, userId));
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate start = LocalDate.parse("2023-02-19");
        LocalDate end = LocalDate.parse("2023-02-19");
        int userId = UserTestData.USER_ID;
        List<Meal> checkedMeals = mealService.getBetweenInclusive(start, end, userId);
        assertMatch(checkedMeals, mealsUser.subList(3, 7));
    }

    @Test
    public void getAll() {
        int userId = UserTestData.USER_ID;
        List<Meal> checkedMeals = mealService.getAll(userId);
        assertMatch(checkedMeals, mealsUser);
    }

    @Test
    @Rollback
    public void update() {
        int userId = UserTestData.USER_ID;
        Meal checkUpdate = getUpdated();
        mealService.update(getUpdated(), userId);
        assertMatch(mealService.get(checkUpdate.getId(), userId), checkUpdate);
    }

    @Test
    @Rollback
    public void create() {
        int userId = UserTestData.USER_ID;
        Meal created = mealService.create(getNew(), userId);
        int newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(mealService.get(newId, userId), newMeal);
    }

    @Test
    @Rollback
    public void deleteNotYours() {
        int wrongUserId = UserTestData.ADMIN_ID;
        assertThrows(NotFoundException.class, () -> mealService.delete(100006, wrongUserId));
    }

    @Test
    public void updateNotYours() {
        int wrongUserId = UserTestData.ADMIN_ID;
        assertThrows(NotFoundException.class, () -> mealService.update(getUpdated(), wrongUserId));
    }

    @Test
    public void getNotYours() {
        int wrongUserId = UserTestData.ADMIN_ID;
        assertThrows(NotFoundException.class, () -> mealService.get(100006, wrongUserId));
    }

    @Test
    @Rollback
    public void createDuplicateDateTime() {
        int userId = UserTestData.USER_ID;
        Meal created = mealService.get(100007, userId);
        created.setDescription("created with same datetime");
        created.setId(null);
        assertThrows(DataAccessException.class, () -> mealService.create(created, userId));
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.get(NOT_FOUND, UserTestData.USER_ID));
    }

    @Test
    @Rollback
    public void updateNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.get(NOT_FOUND, UserTestData.USER_ID));

    }
}