package com.ms.reborn.domain.user.service;

import com.ms.reborn.domain.user.dto.LoginRequest;
import com.ms.reborn.domain.user.dto.UserRequest;
import com.ms.reborn.domain.user.dto.UserResponse;
import com.ms.reborn.domain.user.dto.UserUpdateRequest;
import com.ms.reborn.domain.user.entity.User;
import com.ms.reborn.domain.user.repository.UserRepository;
import com.ms.reborn.global.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public Long signup(UserRequest req) {
        if (userRepository.existsByEmailAndDeletedFalse(req.getEmail()))
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        if (userRepository.existsByNicknameAndDeletedFalse(req.getNickname()))
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");

        String hash = passwordEncoder.encode(req.getPassword());
        User user = User.builder()
                .email(req.getEmail())
                .password(hash)
                .nickname(req.getNickname())
                .deleted(false)
                .build();
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public void updateProfile(Long userId, UserUpdateRequest req) {
        User user = getActiveUser(userId);
        user.setNickname(req.getNickname());
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String oldPw, String newPw) {
        User user = getActiveUser(userId);
        if (!passwordEncoder.matches(oldPw, user.getPassword()))
            throw new IllegalArgumentException("기존 비밀번호 불일치");
        user.setPassword(passwordEncoder.encode(newPw));
        userRepository.save(user);
    }

    @Transactional
    public void withdraw(Long userId, String pw) {
        User user = getActiveUser(userId);

        // 소셜 로그인 유저는 비번 체크 건너뜀
        if (user.getProvider() == null) {
            if (!passwordEncoder.matches(pw, user.getPassword())) {
                throw new IllegalArgumentException("비밀번호 불일치");
            }
        }

        user.setDeleted(true);
        userRepository.save(user);
    }

    public UserResponse getProfile(Long userId) {
        User user = getActiveUser(userId);
        return UserResponse.from(user);
    }

    public User findUserEvenIfDeleted(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    public String login(LoginRequest req) {
        User user = userRepository.findByEmailAndDeletedFalse(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return jwtUtil.generateToken(user.getEmail(), user.getId());
    }

    // ❗ 공통적으로 탈퇴 안 한 유저만 가져오게
    public User getActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        if (user.isDeleted()) {
            throw new IllegalArgumentException("탈퇴한 계정입니다.");
        }
        return user;
    }

    @Transactional
    public User findOrCreateOAuthUser(String provider, String providerId, String email) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    // ✅ 탈퇴 유저 복구
                    Optional<User> deletedUserOpt = userRepository.findByEmail(email);
                    if (deletedUserOpt.isPresent()) {
                        User deletedUser = deletedUserOpt.get();
                        deletedUser.setDeleted(false);
                        deletedUser.setProvider(provider);
                        deletedUser.setProviderId(providerId);
                        deletedUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                        userRepository.save(deletedUser);

                        // ✅ 여기 중요! 복구된 유저 다시 조회해서 return
                        return userRepository.findByEmailAndDeletedFalse(email)
                                .orElseThrow(() -> new RuntimeException("복구 후 유저 조회 실패"));
                    }

                    // ✅ 새 유저
                    User newUser = User.builder()
                            .email(email)
                            .nickname(provider + "_" + providerId)
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .provider(provider)
                            .providerId(providerId)
                            .deleted(false)
                            .build();
                    return userRepository.save(newUser);
                });
    }


}
