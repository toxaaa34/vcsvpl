package ru.artosoft.vcsvpl.controller;

import org.hibernate.Hibernate;
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
import ru.artosoft.vcsvpl.entity.BranchEntity;
import ru.artosoft.vcsvpl.entity.CommitEntity;
import ru.artosoft.vcsvpl.entity.ProjectEntity;
import ru.artosoft.vcsvpl.entity.UserEntity;
import ru.artosoft.vcsvpl.repository.BranchRepository;
import ru.artosoft.vcsvpl.repository.CommitRepository;
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
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ProjectController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private CommitRepository commitRepository;

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
        ProjectEntity project = new ProjectEntity(user.getId(), user.getUsername(), projectName.toLowerCase().trim().replace(" ", "_"), description, isPublic);
        projectRepository.save(project);
//        ProjectService.createDefaultRepository(project);
        BranchEntity branch = new BranchEntity(project.getId(), project.getFullProjectName(), "main");
        branchRepository.save(branch);
        return "redirect:/repository/" + project.getFullProjectName() + "/main";
    }

    @GetMapping("/repository/{authorName}/{projectName}")
    public String repositoryMainDetails(@PathVariable(value = "authorName") String authorName, @PathVariable(value = "projectName") String projectName, Model model, Principal principal) {
        String fullProjectName = authorName + "/" + projectName;
        if (!projectRepository.existsByFullProjectName(fullProjectName)) {
            return "repositorynotfound";
        }
        return "redirect:/repository/" + fullProjectName + "/main";
    }

    @PostMapping("/repository/{authorName}/{projectName}/createBranch")
    public String createBranch(@PathVariable(value = "authorName") String authorName,
                               @PathVariable(value = "projectName") String projectName,
                               @RequestParam String branchName, @RequestParam String branchSource,
                               Model model) {
        String fullProjectName = authorName + "/" + projectName;
        if (!projectRepository.existsByFullProjectName(fullProjectName)) {
            return "repositorynotfound";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);
        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        BranchEntity branch = new BranchEntity(project.get().getId(), fullProjectName, branchName);
        branchRepository.save(branch);

        CommitEntity commit = new CommitEntity();
        if (commitRepository.existsCommitEntitiesByFullProjectNameAndBranchName(fullProjectName, branchSource)) {
            CommitEntity lastCommit = commitRepository.findTopByFullProjectNameAndBranchNameOrderByCommitIdDesc(fullProjectName, branchSource);
            commit.setCommitId(lastCommit.getCommitId() + 1);
            commit.setLastCommitId(lastCommit.getId());
            commit.setOldContent(lastCommit.getOldContent());
            commit.setCommitName(lastCommit.getCommitName());
            commit.setAuthorId(user.getId());
            commit.setAuthorName(user.getUsername());
            commit.setDescription(lastCommit.getDescription());
            commit.setProjectId(project.get().getId());
            commit.setFullProjectName(fullProjectName);
            commit.setBranchId(branch.getId());
            commit.setBranchName(branch.getBranchName());
            commit.setCommitDateTime(LocalDateTime.now());
            commit.setFileName(lastCommit.getFileName());
            commit.setNewContent(lastCommit.getNewContent());
            commitRepository.save(commit);
        }


        return "redirect:/repository/" + fullProjectName + "/" + branchName;
    }

    @GetMapping("/repository/{authorName}/{projectName}/{branch}")
    public String repositoryDetails(@PathVariable(value = "authorName") String authorName, @PathVariable(value = "projectName") String projectName, @PathVariable(value = "branch") String branch, Model model, Principal principal) {
        String fullProjectName = authorName + "/" + projectName;
        if (!projectRepository.existsByFullProjectName(fullProjectName)) {
            return "repositorynotfound";
        }
        if (!branchRepository.existsByFullProjectNameAndBranchName(fullProjectName, branch)) {
            //изменить на "не найдена ветка"
            return "repositorynotfound";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        String author = project.get().getAuthorName();
        ArrayList<ProjectEntity> res = new ArrayList<>();
        project.ifPresent(res::add);

//        String folderPath = "src/projects/" + authorName + "/" + projectName + "/main";
//        File folder = new File(folderPath);
//        File[] listOfFiles = folder.listFiles();
//        ArrayList<String> fileNames = new ArrayList<>();
//
//        if (listOfFiles != null) {
//            for (File file : listOfFiles) {
//                if (file.isFile()) {
//                    fileNames.add(file.getName());
//                }
//            }
//        }

        CommitEntity commit = commitRepository.findTopByFullProjectNameAndBranchNameOrderByCommitIdDesc(fullProjectName, branch);
        ;
        if (!commitRepository.existsCommitEntitiesByFullProjectNameAndBranchName(fullProjectName, branch)) {
            String nullFile = "Проект пуст. Сделайте первый коммит и добавьте файлы в проект";
            model.addAttribute("nullFile", nullFile);
        }
        Iterable<BranchEntity> branches = branchRepository.getAllByFullProjectName(fullProjectName);
        List<String> branchesList = new ArrayList<>();
        branches.forEach(branchEntity -> branchesList.add(branchEntity.getBranchName()));

        model.addAttribute("fullProjectName", project.get().getFullProjectName());
        model.addAttribute("info", res);
        model.addAttribute("author", author);
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("currentBranch", branch);
        model.addAttribute("allBranches", branchesList);
        if (commit != null) {
            model.addAttribute("fileName", commit.getFileName());
        }
        return "project-details";
    }


    @PostMapping("/repository/{authorName}/{projectName}/{branch}/commit")
    public String makeCommit(@PathVariable(value = "authorName") String authorName, @PathVariable(value = "projectName") String projectName, @PathVariable(value = "branch") String branch, @RequestParam String commitName, @RequestParam String description, @RequestParam("file") MultipartFile file) {
        String fullProjectName = authorName + "/" + projectName;
        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        BranchEntity branchEntity = branchRepository.findByFullProjectNameAndBranchName(fullProjectName, branch);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);
        if (!user.getId().equals(project.get().getAuthorId())) {
            return "repositorynotfound";
        }
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

//        String filePath = "src/projects/" + authorName + "/" + projectName + "/main/" + file.getOriginalFilename();
//        File newFile = new File(filePath);
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(newFile))) {
//            writer.write(new String(file.getBytes()));
//        }
        if (file == null && file.getOriginalFilename().isEmpty()) {
            //исправить, тут должна быть ошибка
            return "redirect:/repository/{authorName}/{projectName}/" + branch;
        }
        CommitEntity commit = new CommitEntity();
        try {
            if (commitRepository.existsCommitEntitiesByFullProjectNameAndBranchName(fullProjectName, branch)) {
                CommitEntity lastCommit = commitRepository.findTopByFullProjectNameAndBranchNameOrderByCommitIdDesc(fullProjectName, branch);
                commit.setCommitId(lastCommit.getCommitId() + 1);
                commit.setLastCommitId(lastCommit.getId());
                commit.setOldContent(lastCommit.getNewContent());
            } else {
                commit.setCommitId(1L);
                commit.setLastCommitId(null);
                commit.setOldContent(null);
            }
            commit.setCommitName(commitName);
            commit.setAuthorId(user.getId());
            commit.setAuthorName(user.getUsername());
            commit.setDescription(description);
            commit.setProjectId(project.get().getId());
            commit.setFullProjectName(fullProjectName);
            commit.setBranchId(branchEntity.getId());
            commit.setBranchName(branch);
            commit.setCommitDateTime(LocalDateTime.now());
            commit.setFileName(file.getOriginalFilename());
            commit.setNewContent(file.getBytes());
            commitRepository.save(commit);
        } catch (IOException e) {

        }

        return "redirect:/repository/{authorName}/{projectName}/" + branch;
    }

    @GetMapping("/repository/{authorName}/{projectName}/{branch}/{fileName}")
    public String blogDetails(@PathVariable(value = "authorName") String authorName,
                              @PathVariable(value = "projectName") String projectName,
                              @PathVariable(value = "branch") String branch,
                              @PathVariable(value = "fileName") String fileName, Model model, Principal principal) {
        String fullProjectName = authorName + "/" + projectName;

        if (!projectRepository.existsByFullProjectName(fullProjectName)) {
            return "repositorynotfound";
        }
        if (!commitRepository.existsCommitEntitiesByFullProjectNameAndBranchName(fullProjectName, branch)) {
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


//        String filePath = "src/projects/" + authorName + "/" + projectName + "/main/" + fileName;
//

        CommitEntity commit = commitRepository.findTopByFullProjectNameAndBranchNameOrderByCommitIdDesc(fullProjectName, branch);
        byte[] fileText = commit.getNewContent();

        StringBuilder jsCode = DrawProgramService.drawFullCode(fileText);
        String js = jsCode.toString();


        String fileContent = new String(commit.getNewContent());
        model.addAttribute("fileContent", fileContent);
        if (fileContent.isEmpty()) {
            model.addAttribute("error", "Ошибка при чтении файла");
        }


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

    @GetMapping("/repository/{authorName}/{projectName}/commits")
    public String repositoryCommits(@PathVariable(value = "authorName") String authorName,
                                    @PathVariable(value = "projectName") String projectName,
                                    Model model, Principal principal) {
        String fullProjectName = authorName + "/" + projectName;
        if (!projectRepository.existsByFullProjectName(fullProjectName)) {
            return "repositorynotfound";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        String author = project.get().getAuthorName();
        ArrayList<ProjectEntity> res = new ArrayList<>();
        project.ifPresent(res::add);

        List<CommitEntity> commits = commitRepository.findAllByFullProjectNameOrderByCommitDateTimeDesc(fullProjectName);

        Iterable<BranchEntity> branches = branchRepository.getAllByFullProjectName(fullProjectName);
        List<String> branchesList = new ArrayList<>();
        branches.forEach(branchEntity -> branchesList.add(branchEntity.getBranchName()));

        model.addAttribute("fullProjectName", project.get().getFullProjectName());
        model.addAttribute("info", res);
        model.addAttribute("author", author);
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("allBranches", branchesList);
        model.addAttribute("commitsInfo", commits);
        return "commits-history";
    }
}
