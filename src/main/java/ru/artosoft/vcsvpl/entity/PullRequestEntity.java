package ru.artosoft.vcsvpl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="t_pull_request")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class PullRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private Long authorId;
    @Column(nullable = false)
    private String authorName;

    private String description;

    @Column(nullable = false)
    private Long projectId;
    @Column(nullable = false)
    private String fullProjectName;
    @Column(nullable = false)
    private Long branchIdFrom;
    @Column(nullable = false)
    private String branchNameFrom;
    @Column(nullable = false)
    private Long commitIdFrom;
    @Column(nullable = false)
    private Long branchIdTo;
    @Column(nullable = false)
    private String branchNameTo;
    @Column(nullable = false)
    private Long commitIdTo;
    @Column(nullable = false)
    private LocalDateTime commitDateTime;
    @Column(nullable = false)
    private Boolean isClosed;

    public PullRequestEntity(String title, Long authorId, String authorName, String description, Long projectId, String fullProjectName, Long branchIdFrom, String branchNameFrom, Long commitIdFrom, Long branchIdTo, String branchNameTo, Long commitIdTo, LocalDateTime commitDateTime, Boolean isClosed) {
        this.title = title;
        this.authorId = authorId;
        this.authorName = authorName;
        this.description = description;
        this.projectId = projectId;
        this.fullProjectName = fullProjectName;
        this.branchIdFrom = branchIdFrom;
        this.branchNameFrom = branchNameFrom;
        this.commitIdFrom = commitIdFrom;
        this.branchIdTo = branchIdTo;
        this.branchNameTo = branchNameTo;
        this.commitIdTo = commitIdTo;
        this.commitDateTime = commitDateTime;
        this.isClosed = isClosed;
    }
}
