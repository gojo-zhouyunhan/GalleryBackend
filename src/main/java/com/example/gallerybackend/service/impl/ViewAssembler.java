package com.example.gallerybackend.service.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ViewAssembler {

    private final JdbcTemplate jdbcTemplate;

    public ViewAssembler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> requireUser(String username) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id, username, password, status, gallery_permission from users where username = ?",
                username
        );
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("user not found: " + username);
        }
        return rows.get(0);
    }

    public Map<String, Object> buildUserView(String username) {
        Map<String, Object> user = requireUser(username);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", ((Number) user.get("id")).longValue());
        result.put("username", user.get("username"));
        result.put("password", user.get("password"));
        result.put("status", String.valueOf(user.get("status")));
        result.put("galleryPermission", user.get("gallery_permission"));
        result.put("friends", listFriendUsernames(((Number) user.get("id")).longValue()));
        result.put("Wall", buildWallMap(((Number) user.get("id")).longValue()));
        return result;
    }

    public List<String> listFriendUsernames(Long userId) {
        return jdbcTemplate.query(
                "select u.username from friendships f join users u on f.friend_id = u.id where f.user_id = ? order by u.id",
                (rs, rowNum) -> rs.getString("username"),
                userId
        );
    }

    public Map<String, List<Map<String, Object>>> buildWallMap(Long userId) {
        Map<String, List<Map<String, Object>>> wall = new LinkedHashMap<>();
        wall.put("Right", new ArrayList<>());
        wall.put("Left", new ArrayList<>());
        wall.put("Front", new ArrayList<>());
        wall.put("Back", new ArrayList<>());

        List<Map<String, Object>> placements = jdbcTemplate.queryForList(
                "select painting_id, wall_name, position_x, position_y, position_z, scale_x, scale_y, scale_z, size_width, size_height " +
                        "from wall_placements where user_id = ? order by id",
                userId
        );
        for (Map<String, Object> placement : placements) {
            String wallName = String.valueOf(placement.get("wall_name"));
            wall.computeIfAbsent(wallName, key -> new ArrayList<>()).add(toPlacementMap(placement));
        }
        return wall;
    }

    public Map<String, Object> buildPaintingView(Map<String, Object> row) {
        Long paintingId = ((Number) row.get("id")).longValue();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", paintingId);
        result.put("name", row.get("name"));
        result.put("imgUrl", row.get("img_url"));
        result.put("author", row.get("author"));
        result.put("createTime", row.get("create_time"));
        result.put("type", row.get("type"));
        result.put("status", String.valueOf(row.get("status")));
        result.put("likeCount", ((Number) row.get("like_count")).intValue());
        result.put("commentCount", ((Number) row.get("comment_count")).intValue());

        List<Map<String, Object>> comments = jdbcTemplate.queryForList(
                "select id, painting_id, commentator, content, create_time from painting_comments where painting_id = ? order by id",
                paintingId
        ).stream().map(this::toCommentMap).toList();
        result.put("comments", comments);

        Map<String, Object> commentContent = new LinkedHashMap<>();
        if (comments.isEmpty()) {
            commentContent.put("Commentator", "null");
            commentContent.put("comment", "null");
        } else {
            Map<String, Object> last = comments.get(comments.size() - 1);
            commentContent.put("Commentator", last.get("commentator"));
            commentContent.put("comment", last.get("content"));
        }
        result.put("commentContent", commentContent);
        return result;
    }

    public Map<String, Object> toCommentMap(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", ((Number) row.get("id")).longValue());
        result.put("paintingId", ((Number) row.get("painting_id")).longValue());
        result.put("commentator", row.get("commentator"));
        result.put("content", row.get("content"));
        result.put("createTime", row.get("create_time"));
        return result;
    }

    public Map<String, Object> toPlacementMap(Map<String, Object> row) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("paintingId", ((Number) row.get("painting_id")).longValue());

        Map<String, Object> position = new LinkedHashMap<>();
        position.put("x", toDouble(row.get("position_x"), 0D));
        position.put("y", toDouble(row.get("position_y"), 0D));
        position.put("z", toDouble(row.get("position_z"), 0D));
        result.put("position", position);

        Map<String, Object> scale = new LinkedHashMap<>();
        scale.put("x", toDouble(row.get("scale_x"), 1D));
        scale.put("y", toDouble(row.get("scale_y"), 1D));
        scale.put("z", toDouble(row.get("scale_z"), 1D));
        result.put("scale", scale);

        Map<String, Object> size = new LinkedHashMap<>();
        size.put("width", toDouble(row.get("size_width"), 4D));
        size.put("height", toDouble(row.get("size_height"), 3D));
        result.put("size", size);
        return result;
    }

    public double toDouble(Object value, double fallback) {
        return value instanceof Number number ? number.doubleValue() : fallback;
    }

    public String normalizeWallName(String wallName) {
        return switch (wallName) {
            case "front", "Front" -> "Front";
            case "back", "Back" -> "Back";
            case "left", "Left" -> "Left";
            case "right", "Right" -> "Right";
            default -> throw new IllegalArgumentException("invalid wall name: " + wallName);
        };
    }
}
