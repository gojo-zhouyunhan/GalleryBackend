package com.example.gallerybackend.service;

import java.util.List;
import java.util.Map;

public interface PaintingService {

    List<Map<String, Object>> listPaintings();

    List<Map<String, Object>> listPaintingsByType(String type);

    Map<String, Object> getPainting(Long paintingId);

    Map<String, Object> likePainting(Long paintingId);

    List<Map<String, Object>> listComments(Long paintingId);

    List<Map<String, Object>> addComment(Long paintingId, String commentator, String content);
}
