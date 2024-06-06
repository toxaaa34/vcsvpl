package ru.artosoft.vcsvpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.artosoft.vcsvpl.entity.BranchEntity;
import ru.artosoft.vcsvpl.entity.UserEntity;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, Long> {
    boolean existsByFullProjectNameAndBranchName(String fullProjectName, String BranchName);

    BranchEntity findByFullProjectNameAndBranchName(String fullProjectName, String BranchName);

    Iterable<BranchEntity> getAllByFullProjectName(String fullProjectName);
}
