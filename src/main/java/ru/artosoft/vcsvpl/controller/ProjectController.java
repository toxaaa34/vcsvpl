package ru.artosoft.vcsvpl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.artosoft.vcsvpl.entity.ProjectEntity;
import ru.artosoft.vcsvpl.entity.UserEntity;
import ru.artosoft.vcsvpl.repository.ProjectRepository;
import ru.artosoft.vcsvpl.repository.UserRepository;
import ru.artosoft.vcsvpl.service.DrawProgramService;
import ru.artosoft.vcsvpl.service.ProjectService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

@Controller
public class ProjectController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/welcome")
    public String welcome(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userRepository.findByUsername(authentication.getName());
        Iterable<ProjectEntity> projects = projectRepository.findAllByAuthorId(user.getId());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("projects", projects);
        return "welcome";
    }

    @GetMapping("/repository/add")
    public String projectAdd(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);
        model.addAttribute("username", user.getUsername());
        return "repository-add";
    }

    @PostMapping("/repository/add")
    public String projectAdd(@RequestParam String projectName, @RequestParam String description, @RequestParam boolean isPublic, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);
        ProjectEntity project = new ProjectEntity(user.getId(), user.getUsername(),projectName.toLowerCase().trim().replace(" ", "_"), description, isPublic);
        projectRepository.save(project);
        ProjectService.createDefaultRepository(project);
        return "redirect:/repository/" + project.getFullProjectName();
    }

    @GetMapping("/repository/{authorName}/{projectName}")
    public String repositoryDetails(@PathVariable(value = "authorName") String authorName,@PathVariable(value = "projectName") String projectName, Model model, Principal principal) {
        String fullProjectName = authorName + "/" + projectName;
        if(!projectRepository.existsByFullProjectName(fullProjectName)) {
            return "repositorynotfound";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        String author = project.get().getAuthorName();
        ArrayList<ProjectEntity> res = new ArrayList<>();
        project.ifPresent(res::add);

        String folderPath = "src/projects/" + authorName + "/" + projectName + "/main";
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }

        model.addAttribute("fullProjectName", project.get().getFullProjectName());
        model.addAttribute("info", res);
        model.addAttribute("author", author);
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("fileNames", fileNames);
        return "project-details";
    }

    @PostMapping("/repository/{authorName}/{projectName}")
    public String uploadFile(@PathVariable(value = "authorName") String authorName, @PathVariable(value = "projectName") String projectName, @RequestParam("file") MultipartFile file) throws IOException {
//        if (file != null && !file.getOriginalFilename().isEmpty()) {
//            File uploadDir = new File("src/projects/" + authorName + "/" + projectName +"/main");
//
//            if (!uploadDir.exists()) {
//                uploadDir.mkdir();
//            }
//
//            String resultFilename = file.getOriginalFilename();
//
//            file.transferTo(new File("src/projects/" + authorName + "/" + projectName +"/main/" +  resultFilename));
//
//        }

        String filePath = "src/projects/" + authorName + "/" + projectName + "/main/" + file.getOriginalFilename();
        File newFile = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(newFile))) {
            writer.write(new String(file.getBytes()));
        }
        return "redirect:/repository/{authorName}/{projectName}";
    }

    @GetMapping("/repository/{authorName}/{projectName}/{fileName}")
    public String blogDetails(@PathVariable(value = "authorName") String authorName,
                              @PathVariable(value = "projectName") String projectName,
                              @PathVariable(value = "fileName") String fileName, Model model, Principal principal) {
        String fullProjectName = authorName + "/" + projectName;

        if(!projectRepository.existsByFullProjectName(fullProjectName)) {
            return "repositorynotfound";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        String author = project.get().getAuthorName();
        ArrayList<ProjectEntity> res = new ArrayList<>();
        project.ifPresent(res::add);

        String folderPath = "src/projects/" + authorName + "/" + projectName + "/main";
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }

        String filePath = "src/projects/" + authorName + "/" + projectName + "/main/" + fileName;

        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
            model.addAttribute("fileContent", fileContent);
        } catch (IOException e) {
            model.addAttribute("error", "Ошибка при чтении файла");
        }

        StringBuilder jsCode = DrawProgramService.drawFullCode(filePath);
        String js = jsCode.toString();

//        try {
//            FileWriter fileWriter = new FileWriter("src/main/resources/static/js/drawCode.js");
//            fileWriter.write(js);
//            fileWriter.close();
//            System.out.println("JavaScript file 'drawCode.js' has been created successfully.");
//        } catch (IOException e) {
//            System.err.println("An error occurred while creating the JavaScript file: " + e.getMessage());
//        }

        model.addAttribute("projectName", project.get().getProjectName());
        model.addAttribute("info", res);
        model.addAttribute("author", author);
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("fileNames", fileNames);
//        model.addAttribute("javascriptCommands", "static/js/drawCode.js");
        model.addAttribute("javascriptCommands", js);
        return "file-details";
    }
}
