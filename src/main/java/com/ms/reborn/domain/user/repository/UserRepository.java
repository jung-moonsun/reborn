package com.ms.reborn.domain.user.repository;

import com.ms.reborn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndDeletedFalse(String email);

    Optional<User> findByIdAndDeletedFalse(Long id);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByNicknameAndDeletedFalse(String nickname);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
