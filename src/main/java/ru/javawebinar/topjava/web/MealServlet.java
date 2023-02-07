package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealRepositoryInterface;
import ru.javawebinar.topjava.model.MealsMemoryCRUDRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.slf4j.LoggerFactory.getLogger;


public class MealServlet extends HttpServlet {
    DateTimeFormatter dTFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    static final int CALORIES_PER_DAY = 2000;
    private static final String MEAL_LIST = "/meals.jsp";
    private static final String CREATE_EDIT = "/mealsCreateAndUpdate.jsp";
    private static final Logger log = getLogger(MealServlet.class);
    MealRepositoryInterface mealRepository;

    public MealServlet() {
        super();
        mealRepository = new MealsMemoryCRUDRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("forward to meals");
        String forward;
        String action = request.getParameter("action");
        LocalTime startTime = request.getAttribute("startTime") == null ? LocalTime.MIN : (LocalTime) request.getAttribute("startTime");
        LocalTime endTime = request.getAttribute("endTime") == null ? LocalTime.MAX : (LocalTime) request.getAttribute("endTime");

        if (action != null && action.equalsIgnoreCase("delete")) {
            int mealId = Integer.parseInt(request.getParameter("id"));
            mealRepository.delete(mealId);
            forward = MEAL_LIST;
            request.setAttribute("meals", MealsUtil.filteredByStreams(mealRepository.readAll(), startTime, endTime, CALORIES_PER_DAY));
        } else if (action != null && action.equalsIgnoreCase("update")) {
            forward = CREATE_EDIT;
            int mealId = Integer.parseInt(request.getParameter("id"));
            Meal meal = mealRepository.readById(mealId);
            request.setAttribute("meal", meal);
        } else if (action != null && action.equalsIgnoreCase("create")) {
            forward = CREATE_EDIT;
        } else {
            forward = MEAL_LIST;
            request.setAttribute("meals", MealsUtil.filteredByStreams(mealRepository.readAll(), startTime, endTime, CALORIES_PER_DAY));
        }
        request.getRequestDispatcher(forward).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalTime startTime = request.getAttribute("startTime") == null ? LocalTime.MIN : (LocalTime) request.getAttribute("startTime");
        LocalTime endTime = request.getAttribute("endTime") == null ? LocalTime.MAX : (LocalTime) request.getAttribute("endTime");
        LocalDateTime dateTimeForZero = request.getParameter("dateTime").isEmpty() ? null : LocalDateTime.parse(request.getParameter("dateTime"), dTFmt);
        String descriptionForZero = request.getParameter("description").isEmpty() ? null : request.getParameter("description");

        try{int caloriesForZero = request.getParameter("calories").isEmpty() ? -1 : Integer.parseInt(request.getParameter("calories"));
        Meal zeroMeal = new Meal(dateTimeForZero, descriptionForZero, caloriesForZero);
        if (request.getParameter("id").isEmpty()) {
            if (dateTimeForZero == null || descriptionForZero == null || caloriesForZero <0) {
                request.getRequestDispatcher("/creationError.jsp").forward(request, response);
                return;
            }
            mealRepository.create(zeroMeal);
        } else {
            if (!request.getParameter("calories").isEmpty()&&caloriesForZero<0) {
                request.getRequestDispatcher("/caloriesError.jsp").forward(request, response);
                return;
            }
            mealRepository.update(Integer.parseInt(request.getParameter("id")), zeroMeal);
        }}
        catch(NumberFormatException e){
            request.getRequestDispatcher("/caloriesError.jsp").forward(request, response);
        }
        request.setAttribute("meals", MealsUtil.filteredByStreams(mealRepository.readAll(), startTime, endTime, CALORIES_PER_DAY));
        request.getRequestDispatcher(MEAL_LIST).forward(request, response);
    }
}
