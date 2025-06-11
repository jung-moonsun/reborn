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
        if (!passwordEncoder.matches(pw, user.getPassword()))
            throw new IllegalArgumentException("비밀번호 불일치");
        user.setDeleted(true);
        userRepository.save(user);
    }

    public UserResponse getProfile(Long userId) {
        User user = getActiveUser(userId);
        return UserResponse.from(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email)
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
    private User getActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        if (user.isDeleted()) {
            throw new IllegalArgumentException("탈퇴한 계정입니다.");
        }
        return user;
    }
}
