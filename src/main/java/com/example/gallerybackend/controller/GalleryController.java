package com.example.gallerybackend.controller;

import com.example.gallerybackend.common.ApiResponse;
import com.example.gallerybackend.service.GalleryService;
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
@RequestMapping("/api/galleries")
public class GalleryController {

    private final GalleryService galleryService;

    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @GetMapping("/{username}")
    public ApiResponse<?> getGallery(@PathVariable String username) {
        return ApiResponse.success(galleryService.getGallery(username));
    }

    @PostMapping("/{username}/wall-paintings")
    public ApiResponse<?> hangPainting(@PathVariable String username, @RequestBody Map<String, Object> payload) {
        return ApiResponse.success("hang success", galleryService.hangPainting(username, payload));
    }

    @PutMapping("/{username}/wall-paintings/{paintingId}")
    public ApiResponse<?> updateWallPainting(@PathVariable String username,
                                             @PathVariable Long paintingId,
                                             @RequestBody Map<String, Object> payload) {
        return ApiResponse.success("wall painting updated", galleryService.updateWallPainting(username, paintingId, payload));
    }

    @DeleteMapping("/{username}/walls/{wallName}/paintings/{paintingId}")
    public ApiResponse<?> removeWallPainting(@PathVariable String username,
                                             @PathVariable String wallName,
                                             @PathVariable Long paintingId) {
        galleryService.removeWallPainting(username, wallName, paintingId);
        return ApiResponse.success("wall painting removed", null);
    }
}
