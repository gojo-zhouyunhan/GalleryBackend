package com.example.gallerybackend.controller;

import com.example.gallerybackend.common.ApiResponse;
import com.example.gallerybackend.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<?> listUsers() {
        return ApiResponse.success(userService.listUsers());
    }

    @GetMapping("/{username}")
    public ApiResponse<?> getUser(@PathVariable String username) {
        return ApiResponse.success(userService.getUserByUsername(username));
    }

    @PatchMapping("/{username}/permission")
    public ApiResponse<?> updatePermission(@PathVariable String username, @RequestBody Map<String, Object> payload) {
        Object permission = payload.get("permission");
        if (permission == null || String.valueOf(permission).isBlank()) {
            throw new IllegalArgumentException("permission is required");
        }
        return ApiResponse.success("permission updated", userService.updatePermission(username, String.valueOf(permission)));
    }

    @PostMapping("/{username}/friends")
    public ApiResponse<?> addFriend(@PathVariable String username, @RequestBody Map<String, Object> payload) {
        Object friendUsername = payload.get("friendUsername");
        if (friendUsername == null || String.valueOf(friendUsername).isBlank()) {
            throw new IllegalArgumentException("friendUsername is required");
        }
        return ApiResponse.success("friend added", userService.addFriend(username, String.valueOf(friendUsername)));
    }

    @DeleteMapping("/{username}/friends/{friendUsername}")
    public ApiResponse<?> removeFriend(@PathVariable String username, @PathVariable String friendUsername) {
        return ApiResponse.success("friend removed", userService.removeFriend(username, friendUsername));
    }
}
