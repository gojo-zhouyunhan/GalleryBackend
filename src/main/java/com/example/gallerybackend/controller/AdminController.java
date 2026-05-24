package com.example.gallerybackend.controller;

import com.example.gallerybackend.common.ApiResponse;
import com.example.gallerybackend.service.AdminService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody Map<String, Object> payload) {
        return ApiResponse.success("admin login success", adminService.login(
                requiredText(payload, "username"),
                requiredText(payload, "password")
        ));
    }

    @PostMapping("/logout/{username}")
    public ApiResponse<?> logout(@PathVariable String username) {
        adminService.logout(username);
        return ApiResponse.success("admin logout success", null);
    }

    @GetMapping("/users")
    public ApiResponse<?> listUsers() {
        return ApiResponse.success(adminService.listUsers());
    }

    @PostMapping("/users")
    public ApiResponse<?> createUser(@RequestBody Map<String, Object> payload) {
        return ApiResponse.success("user created", adminService.createUser(payload));
    }

    @PutMapping("/users/{userId}")
    public ApiResponse<?> updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> payload) {
        return ApiResponse.success("user updated", adminService.updateUser(userId, payload));
    }

    @DeleteMapping("/users/{userId}")
    public ApiResponse<?> deleteUser(@PathVariable Long userId) {
        return ApiResponse.success("user deleted", adminService.deleteUser(userId));
    }

    @GetMapping("/paintings")
    public ApiResponse<?> listPaintings() {
        return ApiResponse.success(adminService.listPaintings());
    }

    @PostMapping("/paintings")
    public ApiResponse<?> createPainting(@RequestBody Map<String, Object> payload) {
        return ApiResponse.success("painting created", adminService.createPainting(payload));
    }

    @PutMapping("/paintings/{paintingId}")
    public ApiResponse<?> updatePainting(@PathVariable Long paintingId, @RequestBody Map<String, Object> payload) {
        return ApiResponse.success("painting updated", adminService.updatePainting(paintingId, payload));
    }

    @DeleteMapping("/paintings/{paintingId}")
    public ApiResponse<?> deletePainting(@PathVariable Long paintingId) {
        return ApiResponse.success("painting deleted", adminService.deletePainting(paintingId));
    }

    private String requiredText(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || String.valueOf(value).isBlank()) {
            throw new IllegalArgumentException(key + " is required");
        }
        return String.valueOf(value);
    }
}
