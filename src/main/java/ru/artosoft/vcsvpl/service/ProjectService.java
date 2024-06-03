package ru.artosoft.vcsvpl.service;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import ru.artosoft.vcsvpl.entity.ProjectEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectService {

    public static boolean createDefaultRepository(ProjectEntity project) {
        try {
            Path path = Paths.get("src/projects/" + project.getFullProjectName() +"/main");
            Files.createDirectories(path);

            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
