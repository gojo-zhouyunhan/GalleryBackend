package com.example.gallerybackend.service.impl;

import com.example.gallerybackend.service.AdminService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    private final JdbcTemplate jdbcTemplate;
    private final ViewAssembler viewAssembler;

    public AdminServiceImpl(JdbcTemplate jdbcTemplate, ViewAssembler viewAssembler) {
        this.jdbcTemplate = jdbcTemplate;
        this.viewAssembler = viewAssembler;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id, username, password, nickname, status from admins where username = ?",
                username
        );
        if (rows.isEmpty() || !String.valueOf(rows.get(0).get("password")).equals(password)) {
            throw new IllegalArgumentException("admin username or password is wrong");
        }
        jdbcTemplate.update("update admins set status = '1' where username = ?", username);
        return buildAdminView(username);
    }

    @Override
    public void logout(String username) {
        requireAdmin(username);
        jdbcTemplate.update("update admins set status = '0' where username = ?", username);
    }

    @Override
    public List<Map<String, Object>> listUsers() {
        List<Map<String, Object>> users = jdbcTemplate.queryForList("select username from users order by id");
        return users.stream().map(row -> viewAssembler.buildUserView(String.valueOf(row.get("username")))).toList();
    }

    @Override
    @Transactional
    public List<Map<String, Object>> createUser(Map<String, Object> payload) {
        String username = requiredText(payload, "username");
        String password = requiredText(payload, "password");
        String galleryPermission = text(payload, "galleryPermission", "public");
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from users where username = ?",
                Integer.class,
                username
        );
        if (count != null && count > 0) {
            throw new IllegalArgumentException("username already exists");
        }
        jdbcTemplate.update(
                "insert into users(username, password, status, gallery_permission) values(?, ?, ?, ?)",
                username,
                password,
                text(payload, "status", "0"),
                galleryPermission
        );
        return listUsers();
    }

    @Override
    @Transactional
    public List<Map<String, Object>> updateUser(Long userId, Map<String, Object> payload) {
        requireUserId(userId);
        String username = requiredText(payload, "username");
        Integer conflict = jdbcTemplate.queryForObject(
                "select count(1) from users where username = ? and id <> ?",
                Integer.class,
                username,
                userId
        );
        if (conflict != null && conflict > 0) {
            throw new IllegalArgumentException("username already exists");
        }
        jdbcTemplate.update(
                "update users set username = ?, password = ?, status = ?, gallery_permission = ? where id = ?",
                username,
                requiredText(payload, "password"),
                text(payload, "status", "0"),
                text(payload, "galleryPermission", "public"),
                userId
        );
        return listUsers();
    }

    @Override
    @Transactional
    public List<Map<String, Object>> deleteUser(Long userId) {
        requireUserId(userId);
        jdbcTemplate.update("delete from users where id = ?", userId);
        return listUsers();
    }

    @Override
    public List<Map<String, Object>> listPaintings() {
        return jdbcTemplate.queryForList("select * from paintings order by id")
                .stream()
                .map(viewAssembler::buildPaintingView)
                .toList();
    }

    @Override
    @Transactional
    public List<Map<String, Object>> createPainting(Map<String, Object> payload) {
        jdbcTemplate.update(
                "insert into paintings(name, img_url, author, create_time, type, status, like_count, comment_count) values(?, ?, ?, ?, ?, ?, ?, ?)",
                requiredText(payload, "name"),
                requiredText(payload, "imgUrl"),
                requiredText(payload, "author"),
                requiredText(payload, "createTime"),
                requiredText(payload, "type"),
                text(payload, "status", "0"),
                intValue(payload.get("likeCount"), 0),
                intValue(payload.get("commentCount"), 0)
        );
        return listPaintings();
    }

    @Override
    @Transactional
    public List<Map<String, Object>> updatePainting(Long paintingId, Map<String, Object> payload) {
        requirePaintingId(paintingId);
        jdbcTemplate.update(
                "update paintings set name = ?, img_url = ?, author = ?, create_time = ?, type = ?, status = ?, like_count = ?, comment_count = ? where id = ?",
                requiredText(payload, "name"),
                requiredText(payload, "imgUrl"),
                requiredText(payload, "author"),
                requiredText(payload, "createTime"),
                requiredText(payload, "type"),
                text(payload, "status", "0"),
                intValue(payload.get("likeCount"), 0),
                intValue(payload.get("commentCount"), 0),
                paintingId
        );
        return listPaintings();
    }

    @Override
    @Transactional
    public List<Map<String, Object>> deletePainting(Long paintingId) {
        requirePaintingId(paintingId);
        jdbcTemplate.update("delete from paintings where id = ?", paintingId);
        return listPaintings();
    }

    private Map<String, Object> buildAdminView(String username) {
        Map<String, Object> admin = requireAdmin(username);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", ((Number) admin.get("id")).longValue());
        result.put("username", admin.get("username"));
        result.put("nickname", admin.get("nickname"));
        result.put("status", String.valueOf(admin.get("status")));
        result.put("role", "admin");
        return result;
    }

    private Map<String, Object> requireAdmin(String username) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id, username, password, nickname, status from admins where username = ?",
                username
        );
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("admin not found: " + username);
        }
        return rows.get(0);
    }

    private void requireUserId(Long userId) {
        Integer count = jdbcTemplate.queryForObject("select count(1) from users where id = ?", Integer.class, userId);
        if (count == null || count == 0) {
            throw new IllegalArgumentException("user not found: " + userId);
        }
    }

    private void requirePaintingId(Long paintingId) {
        Integer count = jdbcTemplate.queryForObject("select count(1) from paintings where id = ?", Integer.class, paintingId);
        if (count == null || count == 0) {
            throw new IllegalArgumentException("painting not found: " + paintingId);
        }
    }

    private String requiredText(Map<String, Object> payload, String key) {
        String value = text(payload, key, null);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(key + " is required");
        }
        return value;
    }

    private String text(Map<String, Object> payload, String key, String fallback) {
        Object value = payload.get(key);
        return value == null ? fallback : String.valueOf(value);
    }

    private int intValue(Object value, int fallback) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null || String.valueOf(value).isBlank()) {
            return fallback;
        }
        return Integer.parseInt(String.valueOf(value));
    }
}
