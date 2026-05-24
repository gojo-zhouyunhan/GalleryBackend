package com.example.gallerybackend.controller;

import com.example.gallerybackend.common.ApiResponse;
import com.example.gallerybackend.service.MusicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    private final MusicService musicService;

    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping
    public ApiResponse<?> listMusic() {
        return ApiResponse.success(musicService.listMusic());
    }
}
