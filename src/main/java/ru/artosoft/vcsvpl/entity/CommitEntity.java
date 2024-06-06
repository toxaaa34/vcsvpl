package ru.artosoft.vcsvpl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;
import java.time.LocalDateTime;

@Entity
@Table(name="t_commit")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class CommitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long commitId;
    @Column(nullable = false)
    private String commitName;
    @Column(nullable = false)
    private Long authorId;
    @Column(nullable = false)
    private String authorName;

    private String description;

    private Long lastCommitId;
    @Column(nullable = false)
    private Long projectId;
    @Column(nullable = false)
    private String fullProjectName;
    @Column(nullable = false)
    private Long branchId;
    @Column(nullable = false)
    private String branchName;
    @Column(nullable = false)
    private LocalDateTime commitDateTime;

    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    @Lob
    private byte[] newContent;
    @Lob
    private byte[] oldContent;
}
