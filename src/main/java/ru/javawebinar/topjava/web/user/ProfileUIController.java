package ru.javawebinar.topjava.web.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/profile")
public class ProfileUIController extends AbstractUserController {

    @GetMapping
    public String profile() {
        return "profile";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String updateProfile(@Valid UserTo userTo, BindingResult result, SessionStatus status) throws BindException {
        if (result.hasErrors()) {
            throw new BindException(result);
//            return "profile";
        } else {
            super.update(userTo, SecurityUtil.authUserId());
            SecurityUtil.get().setTo(userTo);
            status.setComplete();
            return "redirect:/meals";
        }
    }

    @GetMapping("/register")
    public String register(ModelMap model) {
        model.addAttribute("userTo", new UserTo());
        model.addAttribute("register", true);
        return "profile";
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String saveRegister(@Valid UserTo userTo, BindingResult result, SessionStatus status, ModelMap model) throws BindException {
        if (result.hasErrors()) {
            throw new BindException(result);
//            model.addAttribute("register", true);
//            return "profile";
        } else {
            super.create(userTo);
            status.setComplete();
            return "redirect:/login?message=app.registered&username=" + userTo.getEmail();
        }
    }
}