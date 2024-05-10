package ru.artosoft.vcsvpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.artosoft.vcsvpl.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String name);

    boolean existsByUsername(String name);
    boolean existsByEmail(String email);
}
