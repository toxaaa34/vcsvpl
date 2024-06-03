package ru.artosoft.vcsvpl.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="t_project")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long authorId;
    @Column(nullable = false)
    private String authorName, description;
    @Column(nullable = false)
    private String projectName;
    @Column(nullable = false, unique = true)
    private String fullProjectName;
    @Column(nullable = false)
    private Boolean isPublic;

    public ProjectEntity(Long authorId, String authorName, String projectName, String description, boolean isPublic) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.projectName = projectName;
        this.fullProjectName = authorName + "/" + projectName;
        this.description = description;
        this.isPublic = isPublic;
    }
}
