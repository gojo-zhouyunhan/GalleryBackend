package com.example.gallerybackend.service;

import java.util.List;
import java.util.Map;

public interface AdminService {

    Map<String, Object> login(String username, String password);

    void logout(String username);

    List<Map<String, Object>> listUsers();

    List<Map<String, Object>> createUser(Map<String, Object> payload);

    List<Map<String, Object>> updateUser(Long userId, Map<String, Object> payload);

    List<Map<String, Object>> deleteUser(Long userId);

    List<Map<String, Object>> listPaintings();

    List<Map<String, Object>> createPainting(Map<String, Object> payload);

    List<Map<String, Object>> updatePainting(Long paintingId, Map<String, Object> payload);

    List<Map<String, Object>> deletePainting(Long paintingId);
}
