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
        int mealId = umeal1.getId();
        Meal meal = mealService.get(mealId, userId);
        assertMatch(meal, umeal1);
    }

    @Test
    @Rollback
    public void delete() {
        int userId = UserTestData.USER_ID;
        int mealId = umeal2.getId();
        mealService.delete(mealId, userId);
        assertThrows(NotFoundException.class, () -> mealService.get(mealId, userId));
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate start = LocalDate.of(2023,02,19);
        LocalDate end = LocalDate.of(2023,02,19);
        int userId = UserTestData.USER_ID;
        assertMatch(mealService.getBetweenInclusive(start, end, userId), mealsUserOn19);
    }

    @Test
    public void getAll() {
        int userId = UserTestData.USER_ID;
        assertMatch(mealService.getAll(userId), mealsUser);
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
        assertThrows(NotFoundException.class, () -> mealService.delete(umeal3.getId(), wrongUserId));
    }

    @Test
    public void updateNotYours() {
        int wrongUserId = UserTestData.ADMIN_ID;
        assertThrows(NotFoundException.class, () -> mealService.update(getUpdated(), wrongUserId));
    }

    @Test
    public void getNotYours() {
        int wrongUserId = UserTestData.ADMIN_ID;
        assertThrows(NotFoundException.class, () -> mealService.get(umeal4.getId(), wrongUserId));
    }

    @Test
    @Rollback
    public void createDuplicateDateTime() {
        int userId = UserTestData.USER_ID;
        Meal created = new Meal(umeal6);
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