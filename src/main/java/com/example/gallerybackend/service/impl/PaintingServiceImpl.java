package com.example.gallerybackend.service.impl;

import com.example.gallerybackend.service.PaintingService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class PaintingServiceImpl implements PaintingService {

    private final JdbcTemplate jdbcTemplate;
    private final ViewAssembler viewAssembler;

    public PaintingServiceImpl(JdbcTemplate jdbcTemplate, ViewAssembler viewAssembler) {
        this.jdbcTemplate = jdbcTemplate;
        this.viewAssembler = viewAssembler;
    }

    @Override
    public List<Map<String, Object>> listPaintings() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from paintings order by id");
        return rows.stream().map(viewAssembler::buildPaintingView).toList();
    }

    @Override
    public List<Map<String, Object>> listPaintingsByType(String type) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from paintings where type = ? order by id", type);
        return rows.stream().map(viewAssembler::buildPaintingView).toList();
    }

    @Override
    public Map<String, Object> getPainting(Long paintingId) {
        return viewAssembler.buildPaintingView(requirePaintingRow(paintingId));
    }

    @Override
    @Transactional
    public Map<String, Object> likePainting(Long paintingId) {
        requirePaintingRow(paintingId);
        jdbcTemplate.update("update paintings set like_count = like_count + 1 where id = ?", paintingId);
        return getPainting(paintingId);
    }

    @Override
    public List<Map<String, Object>> listComments(Long paintingId) {
        requirePaintingRow(paintingId);
        return jdbcTemplate.queryForList(
                "select id, painting_id, commentator, content, create_time from painting_comments where painting_id = ? order by id",
                paintingId
        ).stream().map(viewAssembler::toCommentMap).toList();
    }

    @Override
    @Transactional
    public List<Map<String, Object>> addComment(Long paintingId, String commentator, String content) {
        requirePaintingRow(paintingId);
        jdbcTemplate.update(
                "insert into painting_comments(painting_id, commentator, content, create_time) values(?, ?, ?, now())",
                paintingId,
                commentator,
                content
        );
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from painting_comments where painting_id = ?",
                Integer.class,
                paintingId
        );
        jdbcTemplate.update("update paintings set comment_count = ? where id = ?", count == null ? 0 : count, paintingId);
        return listComments(paintingId);
    }

    private Map<String, Object> requirePaintingRow(Long paintingId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from paintings where id = ?", paintingId);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("painting not found: " + paintingId);
        }
        return rows.get(0);
    }
}
