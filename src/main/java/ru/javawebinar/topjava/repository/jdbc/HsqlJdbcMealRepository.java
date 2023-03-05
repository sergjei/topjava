package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Profile("hsqldb")
public class HsqlJdbcMealRepository extends JdbcMealRepository {
    public HsqlJdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    public Meal save(Meal meal, int userId) {
        unifiedDateTime = Timestamp.valueOf(meal.getDateTime());
        return super.save(meal, userId);
    }

    @Override
    public <T> List<Meal> getBetweenHalfOpen(T startDateTime, T endDateTime, int userId) {
        return super.getBetweenHalfOpen(Timestamp.valueOf((LocalDateTime) startDateTime),
                Timestamp.valueOf((LocalDateTime) endDateTime), userId);
    }
}
