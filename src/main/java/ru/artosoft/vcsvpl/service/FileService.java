package ru.artosoft.vcsvpl.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileService {
    public void saveTextFile(MultipartFile file) throws IOException {
        String filePath = "путь_к_папке_с_файлами/" + file.getOriginalFilename();
        File newFile = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(newFile))) {
            writer.write(new String(file.getBytes()));
        }
    }
}
