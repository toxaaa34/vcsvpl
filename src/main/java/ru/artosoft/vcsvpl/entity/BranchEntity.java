package ru.artosoft.vcsvpl.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="t_branch")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long projectId;
    @Column(nullable = false)
    private String fullProjectName;
    @Column(nullable = false)
    private String branchName;

    public BranchEntity(Long projectId, String fullProjectName, String branchName) {
        this.projectId = projectId;
        this.fullProjectName = fullProjectName;
        this.branchName = branchName;
    }
}
