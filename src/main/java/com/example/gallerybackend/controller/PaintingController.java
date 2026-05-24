package com.example.gallerybackend.controller;

import com.example.gallerybackend.common.ApiResponse;
import com.example.gallerybackend.service.PaintingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/paintings")
public class PaintingController {

    private final PaintingService paintingService;

    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping
    public ApiResponse<?> listPaintings() {
        return ApiResponse.success(paintingService.listPaintings());
    }

    @GetMapping("/type/{type}")
    public ApiResponse<?> listByType(@PathVariable String type) {
        return ApiResponse.success(paintingService.listPaintingsByType(type));
    }

    @GetMapping("/{paintingId}")
    public ApiResponse<?> getPainting(@PathVariable Long paintingId) {
        return ApiResponse.success(paintingService.getPainting(paintingId));
    }

    @PostMapping("/{paintingId}/like")
    public ApiResponse<?> likePainting(@PathVariable Long paintingId) {
        return ApiResponse.success("like success", paintingService.likePainting(paintingId));
    }

    @GetMapping("/{paintingId}/comments")
    public ApiResponse<?> listComments(@PathVariable Long paintingId) {
        return ApiResponse.success(paintingService.listComments(paintingId));
    }

    @PostMapping("/{paintingId}/comments")
    public ApiResponse<?> addComment(@PathVariable Long paintingId, @RequestBody Map<String, Object> payload) {
        Object commentator = payload.get("commentator");
        Object content = payload.get("content");
        if (commentator == null || String.valueOf(commentator).isBlank()) {
            throw new IllegalArgumentException("commentator is required");
        }
        if (content == null || String.valueOf(content).isBlank()) {
            throw new IllegalArgumentException("content is required");
        }
        return ApiResponse.success("comment success", paintingService.addComment(
                paintingId,
                String.valueOf(commentator),
                String.valueOf(content)
        ));
    }
}
