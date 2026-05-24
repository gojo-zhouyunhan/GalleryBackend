package com.example.gallerybackend.service.impl;

import com.example.gallerybackend.service.GalleryService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GalleryServiceImpl implements GalleryService {

    private final JdbcTemplate jdbcTemplate;
    private final ViewAssembler viewAssembler;

    public GalleryServiceImpl(JdbcTemplate jdbcTemplate, ViewAssembler viewAssembler) {
        this.jdbcTemplate = jdbcTemplate;
        this.viewAssembler = viewAssembler;
    }

    @Override
    public Map<String, Object> getGallery(String username) {
        Map<String, Object> user = viewAssembler.buildUserView(username);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("user", user);
        result.put("walls", user.get("Wall"));
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> hangPainting(String username, Map<String, Object> payload) {
        Map<String, Object> user = viewAssembler.requireUser(username);
        Long userId = ((Number) user.get("id")).longValue();
        Long paintingId = toLong(payload.get("paintingId"));
        String wallName = viewAssembler.normalizeWallName(String.valueOf(payload.get("wallName")));

        requirePainting(paintingId);
        Integer occupied = jdbcTemplate.queryForObject(
                "select count(1) from wall_placements where painting_id = ?",
                Integer.class,
                paintingId
        );
        Integer existing = jdbcTemplate.queryForObject(
                "select count(1) from wall_placements where user_id = ? and painting_id = ?",
                Integer.class,
                userId,
                paintingId
        );
        if ((occupied != null && occupied > 0) && (existing == null || existing == 0)) {
            throw new IllegalArgumentException("painting already belongs to another gallery wall");
        }

        Map<String, Object> position = mapValue(payload.get("position"));
        Map<String, Object> scale = mapValue(payload.get("scale"));
        Map<String, Object> size = mapValue(payload.get("size"));

        if (existing != null && existing > 0) {
            jdbcTemplate.update(
                    "update wall_placements set wall_name = ?, position_x = ?, position_y = ?, position_z = ?, scale_x = ?, scale_y = ?, scale_z = ?, size_width = ?, size_height = ? where user_id = ? and painting_id = ?",
                    wallName,
                    toDouble(position.get("x"), 0D),
                    toDouble(position.get("y"), 0D),
                    toDouble(position.get("z"), 0D),
                    toDouble(scale.get("x"), 1D),
                    toDouble(scale.get("y"), 1D),
                    toDouble(scale.get("z"), 1D),
                    toDouble(size.get("width"), 4D),
                    toDouble(size.get("height"), 3D),
                    userId,
                    paintingId
            );
        } else {
            jdbcTemplate.update(
                    "insert into wall_placements(user_id, painting_id, wall_name, position_x, position_y, position_z, scale_x, scale_y, scale_z, size_width, size_height) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    userId,
                    paintingId,
                    wallName,
                    toDouble(position.get("x"), 0D),
                    toDouble(position.get("y"), 0D),
                    toDouble(position.get("z"), 0D),
                    toDouble(scale.get("x"), 1D),
                    toDouble(scale.get("y"), 1D),
                    toDouble(scale.get("z"), 1D),
                    toDouble(size.get("width"), 4D),
                    toDouble(size.get("height"), 3D)
            );
        }
        jdbcTemplate.update("update paintings set status = '1' where id = ?", paintingId);
        return getGallery(username);
    }

    @Override
    @Transactional
    public Map<String, Object> updateWallPainting(String username, Long paintingId, Map<String, Object> payload) {
        Map<String, Object> user = viewAssembler.requireUser(username);
        Long userId = ((Number) user.get("id")).longValue();
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from wall_placements where user_id = ? and painting_id = ?",
                Integer.class,
                userId,
                paintingId
        );
        if (count == null || count == 0) {
            throw new IllegalArgumentException("painting is not hanging on this wall");
        }

        String wallName = payload.get("wallName") == null
                ? String.valueOf(jdbcTemplate.queryForObject("select wall_name from wall_placements where user_id = ? and painting_id = ?", String.class, userId, paintingId))
                : viewAssembler.normalizeWallName(String.valueOf(payload.get("wallName")));
        Map<String, Object> position = mapValue(payload.get("position"));
        Map<String, Object> scale = mapValue(payload.get("scale"));
        Map<String, Object> size = mapValue(payload.get("size"));

        jdbcTemplate.update(
                "update wall_placements set wall_name = ?, position_x = coalesce(?, position_x), position_y = coalesce(?, position_y), position_z = coalesce(?, position_z), " +
                        "scale_x = coalesce(?, scale_x), scale_y = coalesce(?, scale_y), scale_z = coalesce(?, scale_z), size_width = coalesce(?, size_width), size_height = coalesce(?, size_height) " +
                        "where user_id = ? and painting_id = ?",
                wallName,
                nullableDouble(position.get("x")),
                nullableDouble(position.get("y")),
                nullableDouble(position.get("z")),
                nullableDouble(scale.get("x")),
                nullableDouble(scale.get("y")),
                nullableDouble(scale.get("z")),
                nullableDouble(size.get("width")),
                nullableDouble(size.get("height")),
                userId,
                paintingId
        );
        return getGallery(username);
    }

    @Override
    @Transactional
    public void removeWallPainting(String username, String wallName, Long paintingId) {
        Map<String, Object> user = viewAssembler.requireUser(username);
        Long userId = ((Number) user.get("id")).longValue();
        jdbcTemplate.update(
                "delete from wall_placements where user_id = ? and wall_name = ? and painting_id = ?",
                userId,
                viewAssembler.normalizeWallName(wallName),
                paintingId
        );
        jdbcTemplate.update("update paintings set status = '0' where id = ?", paintingId);
    }

    private void requirePainting(Long paintingId) {
        Integer count = jdbcTemplate.queryForObject("select count(1) from paintings where id = ?", Integer.class, paintingId);
        if (count == null || count == 0) {
            throw new IllegalArgumentException("painting not found: " + paintingId);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> mapValue(Object value) {
        return value instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }

    private Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private double toDouble(Object value, double fallback) {
        return value instanceof Number number ? number.doubleValue() : fallback;
    }

    private Double nullableDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : null;
    }
}
