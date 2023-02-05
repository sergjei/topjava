package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealsRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;


public class MealServlet extends HttpServlet {
    static final int CALORIES_PER_DAY = 2000;
    private static final Logger log = getLogger(MealServlet.class);
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        log.debug("forward to meals");
        LocalTime startTime = request.getAttribute("startTime")==null?LocalTime.MIN:(LocalTime) request.getAttribute("startTime");
        LocalTime endTime = request.getAttribute("endTime")==null?LocalTime.MAX:(LocalTime) request.getAttribute("endTime");
        request.setAttribute("meals", MealsUtil.filteredByStreams(MealsRepository.getMealsRepository(),startTime,endTime,CALORIES_PER_DAY));
        request.getRequestDispatcher("meals.jsp").forward(request, response);

    }
}
