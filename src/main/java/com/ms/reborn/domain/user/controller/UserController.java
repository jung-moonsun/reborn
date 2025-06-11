package com.ms.reborn.domain.user.controller;

import com.ms.reborn.domain.user.dto.LoginRequest;
import com.ms.reborn.domain.user.dto.UserRequest;
import com.ms.reborn.domain.user.dto.UserResponse;
import com.ms.reborn.domain.user.dto.UserUpdateRequest;
import com.ms.reborn.domain.user.service.UserService;
import com.ms.reborn.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입")
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid UserRequest req) {
        userService.signup(req);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인(JWT 반환)")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String token = userService.login(req);
        return ResponseEntity.ok().body(Map.of("token", token));
    }

    @Operation(summary = "회원정보 수정")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateProfile(
            @PathVariable Long userId,
            @RequestBody @Valid UserUpdateRequest req
    ) {
        userService.updateProfile(userId, req);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "비밀번호 변경")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @RequestParam String oldPw,
            @RequestParam String newPw
    ) {
        userService.changePassword(userId, oldPw, newPw);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 탈퇴")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> withdraw(
            @PathVariable Long userId,
            @RequestParam String pw
    ) {
        userService.withdraw(userId, pw);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 정보 조회")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication auth) {
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
        Long userId = ud.getUser().getId();
        return ResponseEntity.ok(userService.getProfile(userId));
    }

}
