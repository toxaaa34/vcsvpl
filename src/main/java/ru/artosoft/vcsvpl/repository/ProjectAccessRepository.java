package ru.artosoft.vcsvpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.artosoft.vcsvpl.entity.ProjectAccessEntity;

@Repository
public interface ProjectAccessRepository extends JpaRepository<ProjectAccessEntity, Long> {
    Iterable<ProjectAccessEntity> findAllByProjectId(Long projectId);
    ProjectAccessEntity findByUserIdAndFullProjectName(Long userId, String fullProjectName);

    boolean existsByUserIdAndFullProjectName(Long userId, String fullProjectName);
}
