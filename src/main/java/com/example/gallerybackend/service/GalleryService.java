package com.example.gallerybackend.service;

import java.util.Map;

public interface GalleryService {

    Map<String, Object> getGallery(String username);

    Map<String, Object> hangPainting(String username, Map<String, Object> payload);

    Map<String, Object> updateWallPainting(String username, Long paintingId, Map<String, Object> payload);

    void removeWallPainting(String username, String wallName, Long paintingId);
}
