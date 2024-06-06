package ru.artosoft.vcsvpl.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompareCommitsService {

    public Map<Integer, String> compareCommits(byte[] oldCommitFile, byte[] newCommitFile) {
        String fileName1 = "C:\\Users\\Anton Osipov\\Desktop\\VCSVPL\\SimpleVCSVPL\\pom.xml";
        String fileName2 = "C:\\Users\\Anton Osipov\\Desktop\\3.fprg";

        List<String> file1 = new ArrayList<>();

        List<String> file2 = new ArrayList<>();

        try {
            file1 = Files.readAllLines(Paths.get(fileName1));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            file2 = Files.readAllLines(Paths.get(fileName2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int sum = Math.max(file1.size(), file2.size());

        System.out.println(sum);

        Map<Integer,String> differentElements1 = new HashMap<>();
        Map<Integer,String> differentElements2 = new HashMap<>();

        for (String element : file1) {
            if (!file2.contains(element)) {
                differentElements1.put(file1.indexOf(element) + 1,element);
            }
        }

        for (String element : file2) {
            if (!file1.contains(element)) {
                differentElements2.put(file2.indexOf(element) + 1,element);
            }
        }

        // Выводим элементы, которые отличаются в списках
        System.out.println("Элементы, отличающиеся в 1 списке:");
        differentElements1.forEach((key, value) -> System.out.println(key + " " + value));
        System.out.println("Элементы, отличающиеся в 2 списке:");
        differentElements2.forEach((key, value) -> System.out.println(key + " " + value));

        return differentElements1;
    }
}
