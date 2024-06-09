package ru.artosoft.vcsvpl.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="t_access")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ProjectAccessEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long projectId;
    @Column(nullable = false)
    private String fullProjectName;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private String username;

    public ProjectAccessEntity(Long projectId, String fullProjectName, Long userId, String username){
        this.projectId = projectId;
        this.fullProjectName = fullProjectName;
        this.userId = userId;
        this.username = username;
    }
}
