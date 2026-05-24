package com.example.gallerybackend.controller;

import com.example.gallerybackend.common.ApiResponse;
import com.example.gallerybackend.service.AuthService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody Map<String, Object> payload) {
        return ApiResponse.success("login success", authService.login(
                requiredText(payload, "username"),
                requiredText(payload, "password")
        ));
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody Map<String, Object> payload) {
        return ApiResponse.success("register success", authService.register(
                requiredText(payload, "username"),
                requiredText(payload, "password"),
                text(payload, "galleryPermission", "public")
        ));
    }

    @PostMapping("/logout/{username}")
    public ApiResponse<?> logout(@PathVariable String username) {
        authService.logout(username);
        return ApiResponse.success("logout success", null);
    }

    private String requiredText(Map<String, Object> payload, @NotBlank String key) {
        String value = text(payload, key, null);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value;
    }

    private String text(Map<String, Object> payload, String key, String fallback) {
        Object value = payload.get(key);
        return value == null ? fallback : String.valueOf(value);
    }
}
