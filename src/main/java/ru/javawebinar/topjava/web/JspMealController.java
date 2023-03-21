package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping("/meals")
public class JspMealController {
    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @Autowired
    private MealService service;

    @GetMapping("")
    public String getMeals(Model model) {
        log.info("meals");
        model.addAttribute("meals", MealsUtil.getTos(service.getAll(SecurityUtil.authUserId()),
                SecurityUtil.authUserCaloriesPerDay()));
        return "/meals";
    }

    @GetMapping(path = "", params = "action=filter")
    public String getFilteredMeals(Model model, HttpServletRequest request) {
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        log.info("filtered meals");
        List<Meal> meals = service.getBetweenInclusive(startDate, endDate, SecurityUtil.authUserId());
        List<MealTo> filteredMeals = MealsUtil.getFilteredTos(meals, SecurityUtil.authUserCaloriesPerDay(),
                startTime, endTime);
        model.addAttribute("meals", filteredMeals);
        return "/meals";
    }

    @GetMapping(path = "", params = "action=delete")
    public String deleteMeal(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        log.info("delete meal");
        service.delete(id, SecurityUtil.authUserId());
        return "redirect:meals";
    }

    @GetMapping(path = "", params = "action=create")
    public String createMeal(Model model) {
        final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute(meal);
        return "/mealForm";
    }

    @GetMapping(path = "", params = "action=update")
    public String updateMeal(Model model, HttpServletRequest request) {
        final Meal meal = service.get(Integer.parseInt(request.getParameter("id")), SecurityUtil.authUserId());
        model.addAttribute(meal);
        return "/mealForm";
    }

    @PostMapping("")
    public String saveMeal(HttpServletRequest request) {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        if (StringUtils.hasLength(request.getParameter("id"))) {
            meal.setId(Integer.parseInt(request.getParameter("id")));
            service.update(meal, SecurityUtil.authUserId());
        } else {
            service.create(meal, SecurityUtil.authUserId());
        }
        return "redirect:meals";
    }
}
