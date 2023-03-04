package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Meal m WHERE m.id = :id")
    int delete(@Param("id") int id);

    @Transactional
    @Query("SELECT m FROM Meal m WHERE m.user.id=:userId ORDER BY m.dateTime DESC")
    List<Meal> getAll(@Param("userId") int userId);

    @Transactional
    @Query("SELECT m FROM Meal m WHERE m.user.id=:userId AND m.dateTime>=:startDT AND m.dateTime<:endDT ORDER BY m.dateTime DESC")
    List<Meal> getAllForPeriod(@Param("userId") int userId, @Param("startDT")LocalDateTime startDT, @Param("endDT")LocalDateTime endDT);
}
