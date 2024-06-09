package ru.artosoft.vcsvpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.artosoft.vcsvpl.entity.ProjectAccessEntity;
import ru.artosoft.vcsvpl.entity.PullRequestEntity;

@Repository
public interface PullRequestRepository extends JpaRepository<PullRequestEntity, Long> {
    Iterable<PullRequestEntity> findAllByProjectIdAndIsClosed(Long id, boolean isClosed);
}
