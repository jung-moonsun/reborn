package com.ms.reborn.domain.user.repository;

import com.ms.reborn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndDeletedFalse(String email);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByNicknameAndDeletedFalse(String nickname); // 🔧 여기 수정!
}
