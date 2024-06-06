package ru.artosoft.vcsvpl.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.artosoft.vcsvpl.entity.CommitEntity;
import ru.artosoft.vcsvpl.entity.UserEntity;

import java.util.List;

@Repository
@Transactional
public interface CommitRepository extends JpaRepository<CommitEntity, Long> {
    boolean existsCommitEntitiesByFullProjectNameAndBranchName(String fullProjectName, String branchName);

    CommitEntity findTopByFullProjectNameAndBranchNameOrderByCommitIdDesc(String fullProjectName, String branchName);

    List<CommitEntity> findAllByFullProjectNameOrderByCommitDateTimeDesc(String fullProjectName);

}
