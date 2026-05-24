package com.example.gallerybackend.service;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<Map<String, Object>> listUsers();

    Map<String, Object> getUserByUsername(String username);

    Map<String, Object> updatePermission(String username, String permission);

    Map<String, Object> addFriend(String username, String friendUsername);

    Map<String, Object> removeFriend(String username, String friendUsername);
}
