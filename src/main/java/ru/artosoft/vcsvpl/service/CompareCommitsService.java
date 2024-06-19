package ru.artosoft.vcsvpl.service;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;

import java.io.IOException;
import java.util.*;

public class CompareCommitsService {

    public static String compareCommits(byte[] oldCommitFile, byte[] newCommitFile) {
        StringBuilder fullFile = new StringBuilder("");
        try {
            List<String> originalLines = byteToLines(oldCommitFile);
            List<String> modifiedLines = byteToLines(newCommitFile);

            Patch<String> patch = DiffUtils.diff(originalLines, modifiedLines, true);
            for (AbstractDelta<String> delta : patch.getDeltas()) {
                if (delta.getType() == DeltaType.DELETE) {
                    for (Object line : delta.getSource().getLines()) {
                        fullFile.append("-" + line + "\n");
                    }
                } else if (delta.getType() == DeltaType.INSERT) {
                    for (Object line : delta.getTarget().getLines()) {
                        fullFile.append("+" + line + "\n");
                    }
                } else if (delta.getType() == DeltaType.CHANGE) {
                    for (Object line : delta.getTarget().getLines()) {
                        fullFile.append("~" + line + "\n");
                    }
                } else if (delta.getType() == DeltaType.EQUAL) {
                    for (Object line : delta.getTarget().getLines()) {
                        fullFile.append(" " + line + "\n");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fullFile.toString();
    }

    public static String compareCommitsForFlow(byte[] oldCommitFile, byte[] newCommitFile) {
        StringBuilder fullFile = new StringBuilder("");
        try {
            List<String> originalLines = byteToLines(oldCommitFile);
            List<String> modifiedLines = byteToLines(newCommitFile);

            Patch<String> patch = DiffUtils.diff(originalLines, modifiedLines, true);
            int index;
            for (AbstractDelta<String> delta : patch.getDeltas()) {
                if (delta.getType() == DeltaType.DELETE) {
                    for (Object line : delta.getSource().getLines()) {
                        if (line.toString().contains("/>")) {
                            index = line.toString().lastIndexOf("/>");
                            line = line.toString().substring(0, index) + " diff=\"-\"" + line.toString().substring(index);
                        }
                        else if (!line.toString().contains("</")) {
                            index = line.toString().lastIndexOf(">");
                            line = line.toString().substring(0, index) + " diff=\"-\"" + line.toString().substring(index);
                        }
                        fullFile.append(line + "\n");
                    }
                } else if (delta.getType() == DeltaType.INSERT) {
                    for (Object line : delta.getTarget().getLines()) {
                        if (line.toString().contains("/>")) {
                            index = line.toString().lastIndexOf("/>");
                            line = line.toString().substring(0, index) + " diff=\"+\"" + line.toString().substring(index);
                        }
                        else if (!line.toString().contains("</")) {
                            index = line.toString().lastIndexOf(">");
                            line = line.toString().substring(0, index) + " diff=\"+\"" + line.toString().substring(index);
                        }
                        fullFile.append(line + "\n");
                    }
                } else if (delta.getType() == DeltaType.CHANGE) {
                    for (Object line : delta.getTarget().getLines()) {
                        if (line.toString().contains("/>")) {
                            index = line.toString().lastIndexOf("/>");
                            line = line.toString().substring(0, index) + " diff=\"~\"" + line.toString().substring(index);
                        }
                        else if (!line.toString().contains("</")) {
                            index = line.toString().lastIndexOf(">");
                            line = line.toString().substring(0, index) + " diff=\"~\"" + line.toString().substring(index);
                        }
                        fullFile.append(line + "\n");
                    }
                } else if (delta.getType() == DeltaType.EQUAL) {
                    for (Object line : delta.getTarget().getLines()) {
                        fullFile.append(line + "\n");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fullFile.toString();
    }

    private static List<String> byteToLines(byte[] fileText) throws IOException {
        String text = new String(fileText);

        return Arrays.stream(text.split("\n")).toList();

    }
}
