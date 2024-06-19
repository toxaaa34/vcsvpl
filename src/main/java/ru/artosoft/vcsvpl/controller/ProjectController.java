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
import ru.artosoft.vcsvpl.entity.*;
import ru.artosoft.vcsvpl.repository.*;
import ru.artosoft.vcsvpl.service.CompareCommitsService;
import ru.artosoft.vcsvpl.service.DrawProgramService;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    @Autowired
    private ProjectAccessRepository projectAccessRepository;
    @Autowired
    private PullRequestRepository pullRequestRepository;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

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
        BranchEntity branch = new BranchEntity(project.getId(), project.getFullProjectName(), "main");
        branchRepository.save(branch);
        return "redirect:/repository/" + project.getFullProjectName() + "/main";
    }

    @GetMapping("/searchproject")
    public String searchProject(@RequestParam String searchProject, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        Iterable<ProjectEntity> projects = projectRepository.findAllByFullProjectNameContains(searchProject);
        model.addAttribute("projects", projects);
        return "searchresult";
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
            CommitEntity lastCommit = commitRepository.findTopByFullProjectNameAndBranchNameOrderByIdDesc(fullProjectName, branchSource);
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
        if (!projectRepository.existsByFullProjectName(fullProjectName) || !branchRepository.existsByFullProjectNameAndBranchName(fullProjectName, branch)) {
            return "repositorynotfound";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        String author = project.get().getAuthorName();
        ArrayList<ProjectEntity> res = new ArrayList<>();
        project.ifPresent(res::add);

        if (!project.get().getIsPublic()){
            if (!projectAccessRepository.existsByUserIdAndFullProjectName(user.getId(), fullProjectName)) {
                if (!project.get().getAuthorId().equals(user.getId())) {
                    return "accessdenied";
                }
            }
        }

        if (projectAccessRepository.existsByUserIdAndFullProjectName(user.getId(), project.get().getFullProjectName())
                || project.get().getAuthorId().equals(user.getId())) {
            model.addAttribute("buttonAccess", true);
        } else {
            model.addAttribute("buttonAccess", false);
        }

        CommitEntity commit = commitRepository.findTopByFullProjectNameAndBranchNameOrderByIdDesc(fullProjectName, branch);
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
            model.addAttribute("lastEdit", dtf.format(commit.getCommitDateTime()));
            model.addAttribute("commitName", commit.getCommitName());
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

        if (!projectAccessRepository.existsByUserIdAndFullProjectName(user.getId(), fullProjectName)) {
            if (!user.getId().equals(project.get().getAuthorId())) {
                return "accessdenied";
            }
        }

        if (file == null && file.getOriginalFilename().isEmpty()) {
            return "redirect:/repository/{authorName}/{projectName}/" + branch;
        }
        CommitEntity commit = new CommitEntity();
        try {
            if (commitRepository.existsCommitEntitiesByFullProjectNameAndBranchName(fullProjectName, branch)) {
                CommitEntity lastCommit = commitRepository.findTopByFullProjectNameAndBranchNameOrderByIdDesc(fullProjectName, branch);
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

        CommitEntity commit = commitRepository.findTopByFullProjectNameAndBranchNameOrderByIdDesc(fullProjectName, branch);
        byte[] fileText = commit.getNewContent();

        StringBuilder jsCode = DrawProgramService.drawFullCode(fileText, "myCanvas");
        String js = jsCode.toString();


        String fileContent = new String(commit.getNewContent());
        model.addAttribute("fileContent", fileContent);
        if (fileContent.isEmpty()) {
            model.addAttribute("error", "Ошибка при чтении файла");
        }

        model.addAttribute("projectName", project.get().getProjectName());
        model.addAttribute("fullProjectName", fullProjectName);
        model.addAttribute("info", res);
        model.addAttribute("author", author);
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("fileNames", commit.getFileName());
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

    @GetMapping("/repository/{authorName}/{projectName}/commit/{id}")
    public String commitCompareChanges(@PathVariable(value = "authorName") String authorName,
                                       @PathVariable(value = "projectName") String projectName,
                                       @PathVariable(value = "id") Long commitId,
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
        Iterable<BranchEntity> branches = branchRepository.getAllByFullProjectName(fullProjectName);
        List<String> branchesList = new ArrayList<>();
        branches.forEach(branchEntity -> branchesList.add(branchEntity.getBranchName()));

        Optional<CommitEntity> commit = commitRepository.findById(commitId);
        if (commit.get().getOldContent() != null) {
            String codeText = CompareCommitsService.compareCommits(commit.get().getOldContent(), commit.get().getNewContent());

            StringBuilder jsCodeOld = DrawProgramService.drawFullCode(commit.get().getOldContent(), "jsOld");
            String jsOld = jsCodeOld.toString();

            StringBuilder jsCodeNew = DrawProgramService.drawFullCode(commit.get().getNewContent(), "jsNew");
            String jsNew = jsCodeNew.toString();

            String forFlow = CompareCommitsService.compareCommitsForFlow(commit.get().getOldContent(), commit.get().getNewContent());
            StringBuilder jsCodeFullDiff = DrawProgramService.drawFullCode(forFlow.getBytes(), "jsFullDiff");
            String jsFullDiff = jsCodeFullDiff.toString();

            model.addAttribute("codeText", codeText);
            model.addAttribute("javascriptCommandsFullDiff", jsFullDiff);
            model.addAttribute("javascriptCommandsOld", jsOld);
            model.addAttribute("javascriptCommandsNew", jsNew);
        } else {
            String codeText = new String(commit.get().getNewContent());
            StringBuilder jsCodeOld = DrawProgramService.drawFullCode(commit.get().getNewContent(), "jsNew");
            String jsNew = jsCodeOld.toString();

            model.addAttribute("codeText", codeText);
            model.addAttribute("javascriptCommandsNew", jsNew);
        }

        model.addAttribute("fullProjectName", project.get().getFullProjectName());
        model.addAttribute("info", res);
        model.addAttribute("author", author);
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("allBranches", branchesList);
        model.addAttribute("commitInfo", commit);
        return "commit-compare";
    }

    @GetMapping("/repository/{authorName}/{projectName}/settings")
    public String repositorySettings(@PathVariable(value = "authorName") String authorName,
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
        if (!Objects.equals(user.getId(), project.get().getAuthorId())) {
            return "accessdenied";
        }
        ArrayList<ProjectEntity> res = new ArrayList<>();
        project.ifPresent(res::add);

        Iterable<ProjectAccessEntity> projectAccess = projectAccessRepository.findAllByProjectId(project.get().getId());


        model.addAttribute("fullProjectName", project.get().getFullProjectName());
        model.addAttribute("info", res);
        model.addAttribute("author", author);
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("projectAccess", projectAccess);
        return "project-settings";
    }

    @PostMapping("/repository/{authorName}/{projectName}/adduser")
    public String userAccessAdd(@PathVariable(value = "authorName") String authorName,
                                @PathVariable(value = "projectName") String projectName,
                                @RequestParam String username, Model model) {
        String fullProjectName = authorName + "/" + projectName;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        if (!Objects.equals(user.getId(), project.get().getAuthorId())) {
            return "accessdenied";
        }
        UserEntity addUser = userRepository.findByUsername(username);
        if (projectAccessRepository.existsByUserIdAndFullProjectName(addUser.getId(), fullProjectName)) {
            return "redirect:/repository/" + fullProjectName + "/settings";
        }
        ProjectAccessEntity projectAccess = new ProjectAccessEntity(project.get().getId(), project.get().getFullProjectName(),
                addUser.getId(), addUser.getUsername());
        projectAccessRepository.save(projectAccess);

        return "redirect:/repository/" + fullProjectName + "/settings";
    }

    @PostMapping("/repository/{authorName}/{projectName}/deleteuser/{userId}")
    public String userAccessDelete(@PathVariable(value = "authorName") String authorName,
                                   @PathVariable(value = "projectName") String projectName,
                                   @PathVariable(value = "userId") Long userId, Model model) {
        String fullProjectName = authorName + "/" + projectName;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        if (!Objects.equals(user.getId(), project.get().getAuthorId())) {
            return "accessdenied";
        }
        if (userRepository.existsById(userId)) {
            ProjectAccessEntity projectAccess = projectAccessRepository.findByUserIdAndFullProjectName(userId, fullProjectName);
            projectAccessRepository.delete(projectAccess);
        }

        return "redirect:/repository/" + fullProjectName + "/settings";
    }

    @GetMapping("/repository/{authorName}/{projectName}/pulls")
    public String allpullrequest(@PathVariable(value = "authorName") String authorName, @PathVariable(value = "projectName") String projectName, Model model, Principal principal) {
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

        Iterable<PullRequestEntity> openPullRequests = pullRequestRepository.findAllByProjectIdAndIsClosed(project.get().getId(), false);
        model.addAttribute("openPullRequests", openPullRequests);

        Iterable<PullRequestEntity> closedPullRequests = pullRequestRepository.findAllByProjectIdAndIsClosed(project.get().getId(), true);
        model.addAttribute("closedPullRequests", closedPullRequests);

        if (user.getId().equals(project.get().getAuthorId())
                || projectAccessRepository.existsByUserIdAndFullProjectName(user.getId(), fullProjectName)) {
            model.addAttribute("buttonAccess", true);
        }

        Iterable<BranchEntity> branches = branchRepository.getAllByFullProjectName(fullProjectName);
        List<String> branchesList = new ArrayList<>();
        branches.forEach(branchEntity -> branchesList.add(branchEntity.getBranchName()));

        model.addAttribute("fullProjectName", project.get().getFullProjectName());
        model.addAttribute("info", res);
        model.addAttribute("author", author);
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("allBranches", branchesList);

        return "pull-requests";
    }

    @PostMapping("/repository/{authorName}/{projectName}/openpull")
    public String openPull(@PathVariable(value = "authorName") String authorName, @PathVariable(value = "projectName") String projectName
            , @RequestParam String title, @RequestParam String description, @RequestParam String branchFrom
            , @RequestParam String branchTo, Model model) {
        if (branchTo.equals(branchFrom)) {
            return "repositorynotfound";
        }
        String fullProjectName = authorName + "/" + projectName;
        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        CommitEntity commitFrom = commitRepository.findTopByFullProjectNameAndBranchNameOrderByIdDesc(fullProjectName, branchFrom);
        BranchEntity branchEntityFrom = branchRepository.findByFullProjectNameAndBranchName(fullProjectName, branchFrom);
        CommitEntity commitTo = commitRepository.findTopByFullProjectNameAndBranchNameOrderByIdDesc(fullProjectName, branchTo);
        BranchEntity branchEntityTo = branchRepository.findByFullProjectNameAndBranchName(fullProjectName, branchTo);

        if (commitFrom.getNewContent().equals(commitTo.getNewContent())) {
            return "repositorynotfound";
        }
        PullRequestEntity pullRequest = new PullRequestEntity(title, user.getId(), user.getUsername(),
                description, project.get().getId(), project.get().getFullProjectName(), branchEntityFrom.getId(), branchEntityFrom.getBranchName(),
                commitFrom.getId(), branchEntityTo.getId(), branchEntityTo.getBranchName(), commitTo.getId(), LocalDateTime.now(), false);
        pullRequestRepository.save(pullRequest);


        return "redirect:/repository/" + project.get().getFullProjectName() + "/pulls";
    }

    @GetMapping("/repository/{authorName}/{projectName}/pull/{pullId}")
    public String checkPullRequest(@PathVariable(value = "authorName") String authorName, @PathVariable(value = "projectName") String projectName,
                                   @PathVariable(value = "pullId") Long pullId,
                                   Model model, Principal principal) {
        String fullProjectName = authorName + "/" + projectName;
        if (!projectRepository.existsByFullProjectName(fullProjectName) || !pullRequestRepository.existsById(pullId)) {
            return "repositorynotfound";
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        String author = project.get().getAuthorName();
        ArrayList<ProjectEntity> res = new ArrayList<>();
        project.ifPresent(res::add);

        PullRequestEntity pullRequest = pullRequestRepository.getReferenceById(pullId);

        Optional<CommitEntity> commitFrom = commitRepository.findById(pullRequest.getCommitIdFrom());
        Optional<CommitEntity> commitTo = commitRepository.findById(pullRequest.getCommitIdTo());

        String codeText = CompareCommitsService.compareCommits(commitTo.get().getNewContent(), commitFrom.get().getNewContent());

        StringBuilder jsCodeOld = DrawProgramService.drawFullCode(commitTo.get().getNewContent(), "jsOld");
        String jsOld = jsCodeOld.toString();

        StringBuilder jsCodeNew = DrawProgramService.drawFullCode(commitFrom.get().getNewContent(), "jsNew");
        String jsNew = jsCodeNew.toString();

        if (user.getId().equals(project.get().getAuthorId())) {
            model.addAttribute("buttonAccess", true);
        }

        model.addAttribute("codeText", codeText);
        model.addAttribute("javascriptCommandsOld", jsOld);
        model.addAttribute("javascriptCommandsNew", jsNew);

        model.addAttribute("fullProjectName", project.get().getFullProjectName());
        model.addAttribute("info", res);
        model.addAttribute("author", author);
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("pullRequest", pullRequest);


        return "pull-request";
    }

    @PostMapping("/repository/{authorName}/{projectName}/pull/{pullId}/confirm")
    public String confirmPullRequest(@PathVariable(value = "authorName") String authorName, @PathVariable(value = "projectName") String projectName,
                                   @PathVariable(value = "pullId") Long pullId,
                                   Model model) {
        String fullProjectName = authorName + "/" + projectName;
        if (!projectRepository.existsByFullProjectName(fullProjectName) || !pullRequestRepository.existsById(pullId)) {
            return "repositorynotfound";
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        String author = project.get().getAuthorName();
        ArrayList<ProjectEntity> res = new ArrayList<>();
        project.ifPresent(res::add);
        if (!project.get().getAuthorId().equals(user.getId())) {
            return "accessdenied";
        }

        PullRequestEntity pullRequest = pullRequestRepository.getReferenceById(pullId);

        Optional<CommitEntity> commitFrom = commitRepository.findById(pullRequest.getCommitIdFrom());
        Optional<CommitEntity> commitTo = commitRepository.findById(pullRequest.getCommitIdTo());
        CommitEntity oldBranchCommit = commitRepository.findTopByFullProjectNameAndBranchNameOrderByIdDesc(fullProjectName, commitTo.get().getBranchName());
        pullRequest.setIsClosed(true);
        pullRequestRepository.save(pullRequest);
        CommitEntity newCommit = new CommitEntity();
        newCommit.setCommitId(oldBranchCommit.getCommitId() + 1);
        newCommit.setCommitName("Pull request " + pullRequest.getId() + " " + pullRequest.getTitle());
        newCommit.setAuthorId(pullRequest.getAuthorId());
        newCommit.setAuthorName(pullRequest.getAuthorName());
        newCommit.setDescription(pullRequest.getDescription());
        newCommit.setProjectId(project.get().getId());
        newCommit.setFullProjectName(project.get().getFullProjectName());
        newCommit.setBranchId(pullRequest.getBranchIdTo());
        newCommit.setBranchName(pullRequest.getBranchNameTo());
        newCommit.setCommitDateTime(LocalDateTime.now());
        newCommit.setFileName(commitFrom.get().getFileName());
        newCommit.setNewContent(commitFrom.get().getNewContent());
        newCommit.setOldContent(oldBranchCommit.getOldContent());
        commitRepository.save(newCommit);

        return "redirect:/repository/" + fullProjectName + "/main";
    }
    @PostMapping("/repository/{authorName}/{projectName}/pull/{pullId}/close")
    public String cancelPullRequest(@PathVariable(value = "authorName") String authorName, @PathVariable(value = "projectName") String projectName,
                                     @PathVariable(value = "pullId") Long pullId,
                                     Model model) {
        String fullProjectName = authorName + "/" + projectName;
        if (!projectRepository.existsByFullProjectName(fullProjectName) || !pullRequestRepository.existsById(pullId)) {
            return "repositorynotfound";
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        UserEntity user = userRepository.findByUsername(name);

        Optional<ProjectEntity> project = projectRepository.findByFullProjectName(fullProjectName);
        String author = project.get().getAuthorName();
        ArrayList<ProjectEntity> res = new ArrayList<>();
        project.ifPresent(res::add);
        if (!project.get().getAuthorId().equals(user.getId())) {
            return "accessdenied";
        }

        PullRequestEntity pullRequest = pullRequestRepository.getReferenceById(pullId);
        pullRequest.setIsClosed(true);
        pullRequestRepository.save(pullRequest);
        return "redirect:/repository/" + fullProjectName + "/pulls";
    }

}
