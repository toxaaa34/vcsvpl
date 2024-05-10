package ru.artosoft.vcsvpl.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.artosoft.vcsvpl.entity.UserEntity;
import ru.artosoft.vcsvpl.security.UserDetailsImpl;
import ru.artosoft.vcsvpl.service.UserService;


@Controller
@AllArgsConstructor
public class RegistrationController {
    UserService userService;
    @GetMapping("/registration")
    public String registration(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsImpl) return "redirect:/blog";
        model.addAttribute("userEntity", new UserEntity());
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userEntity") @Validated UserEntity user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка регистрации.\nСвяжитесь с администрацией сайта для устранения ошибки");
            return "registration";
        }
        if (user.getUsername().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
            model.addAttribute("error", "Пожалуйста, заполните все поля формы");
            return "registration";
        }
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Данная почта уже зарегистрирована на сайте");
            return "registration";
        }
        if (userService.existsByName(user.getUsername())){
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "registration";
        }
        if (!userService.isValidUsername(user.getUsername())) {
            model.addAttribute("error", "Имя пользователя может содержать только английские буквы и цифры!");
            return "registration";
        }

        userService.saveUser(user);
        return "redirect:/login?registrationSuccess=true&continue";
    }

}
