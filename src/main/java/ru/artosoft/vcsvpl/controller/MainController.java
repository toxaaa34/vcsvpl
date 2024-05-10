package ru.artosoft.vcsvpl.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.artosoft.vcsvpl.entity.UserEntity;
import ru.artosoft.vcsvpl.repository.UserRepository;
import ru.artosoft.vcsvpl.security.UserDetailsImpl;
import ru.artosoft.vcsvpl.service.UserService;


@AllArgsConstructor
@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserService userService;


    @GetMapping("/")
    public String main() {
        return "index";
    }

    @GetMapping("/index")
    public String index() {
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(value = "registrationSuccess", required = false) String registrationSuccess) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsImpl) return "redirect:/admin";
        if (registrationSuccess != null && registrationSuccess.equals("true")) {
            model.addAttribute("registrationSuccess", "Вы успешно зарегистрировались!");
        }
        return "login";
    }


    @GetMapping("/admin")
    public String admin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);
        model.addAttribute("userid", user.getId());
        model.addAttribute("users", userService.getAll());
        model.addAttribute("username", user.getUsername());
        return "admin";
    }

    @PostMapping("/admin/{id}/ban")
    public String ban(@PathVariable(value = "id") long id, Model model) {
        UserEntity user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
        return "redirect:/admin";
    }

    @GetMapping("/blog/profile/{name}")
    public String profile(Model model, @PathVariable(value = "name") String name) {
        UserEntity user = userRepository.findByUsername(name);
        model.addAttribute("user", user);
        return "profile";
    }
}
