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
    private String projectName, description;
    @Column(nullable = false)
    private Boolean isPublic;
}
