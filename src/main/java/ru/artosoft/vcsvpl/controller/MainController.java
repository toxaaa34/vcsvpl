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
import ru.artosoft.vcsvpl.entity.ProjectEntity;
import ru.artosoft.vcsvpl.entity.UserEntity;
import ru.artosoft.vcsvpl.repository.ProjectRepository;
import ru.artosoft.vcsvpl.repository.UserRepository;
import ru.artosoft.vcsvpl.security.UserDetailsImpl;
import ru.artosoft.vcsvpl.service.UserService;


@AllArgsConstructor
@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectRepository projectRepository;

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
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsImpl) return "redirect:/welcome";
        if (registrationSuccess != null && registrationSuccess.equals("true")) {
            model.addAttribute("registrationSuccess", "Вы успешно зарегистрировались!");
        }
        return "login";
    }

    @GetMapping("/profile/{username}")
    public String profile(Model model, @PathVariable(value = "username") String name) {
        UserEntity user = userRepository.findByUsername(name);
        Iterable<ProjectEntity> projects = projectRepository.findAllByAuthorId(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("projects", projects);

        return "profile";
    }
}
