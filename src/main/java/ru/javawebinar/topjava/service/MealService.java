package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;
@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository){
        this.repository = repository;
    }
    public Meal create(Meal meal, int userId){
        return repository.save(new Meal(userId,meal.getDateTime(),meal.getDescription(),meal.getCalories()));
    }

    public void delete(int userId, int id){
        checkNotFoundWithId(repository.delete(userId,id),id);
            }
    public Meal get(int userId, int id){
        return checkNotFoundWithId(repository.get(userId,id),id);}
    public Collection<Meal> getAll(int userId){

        return repository.getAll(userId);}

    public Collection<Meal> getAllFiltered(int userId, LocalDateTime startDate, LocalDateTime endDate, LocalTime startTime, LocalTime endTime){
        startDate = startDate!=null?startDate:LocalDateTime.MIN;
        endDate = endDate!=null?endDate:LocalDateTime.MAX.minusDays(1);
        startTime = startTime!=null?startTime:LocalTime.MIN;
        endTime = endTime!=null?endTime:LocalTime.MAX;
        return repository.getAllFiltered(userId,startDate, endDate, startTime, endTime);}
    public void update(Meal meal, int userId){
        checkNotFoundWithId(repository.save(new Meal(userId,meal.getDateTime(),meal.getDescription(),meal.getCalories())),meal.getId());}
}