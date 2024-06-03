package ru.artosoft.vcsvpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.artosoft.vcsvpl.entity.ProjectEntity;

import java.util.Optional;

@Repository
public interface ProjectRepository extends CrudRepository<ProjectEntity, Long> {
    Iterable<ProjectEntity> findAllByAuthorId(Long id);
    Optional<ProjectEntity> findByFullProjectName(String projectName);
    Iterable<ProjectEntity> findAllByIsPublic(Boolean isPublic);

    boolean existsByFullProjectName(String projectName);
}
