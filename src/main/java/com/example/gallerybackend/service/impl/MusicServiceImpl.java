package com.example.gallerybackend.service.impl;

import com.example.gallerybackend.service.MusicService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MusicServiceImpl implements MusicService {

    private final JdbcTemplate jdbcTemplate;

    public MusicServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Map<String, Object>> listMusic() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select id, name, singer, url from music order by id");
        return rows.stream().map(row -> {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("id", ((Number) row.get("id")).longValue());
            result.put("name", row.get("name"));
            result.put("singer", row.get("singer"));
            result.put("url", row.get("url"));
            return result;
        }).toList();
    }
}
