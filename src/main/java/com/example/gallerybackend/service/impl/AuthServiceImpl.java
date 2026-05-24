package com.example.gallerybackend.service.impl;

import com.example.gallerybackend.service.AuthService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final JdbcTemplate jdbcTemplate;
    private final ViewAssembler viewAssembler;

    public AuthServiceImpl(JdbcTemplate jdbcTemplate, ViewAssembler viewAssembler) {
        this.jdbcTemplate = jdbcTemplate;
        this.viewAssembler = viewAssembler;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "select id, password from users where username = ?",
                username
        );
        if (rows.isEmpty() || !String.valueOf(rows.get(0).get("password")).equals(password)) {
            throw new IllegalArgumentException("username or password is wrong");
        }
        jdbcTemplate.update("update users set status = '1' where username = ?", username);
        return viewAssembler.buildUserView(username);
    }

    @Override
    @Transactional
    public Map<String, Object> register(String username, String password, String galleryPermission) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from users where username = ?",
                Integer.class,
                username
        );
        if (count != null && count > 0) {
            throw new IllegalArgumentException("username already exists");
        }
        jdbcTemplate.update(
                "insert into users(username, password, status, gallery_permission) values(?, ?, '0', ?)",
                username,
                password,
                galleryPermission == null || galleryPermission.isBlank() ? "public" : galleryPermission
        );
        return viewAssembler.buildUserView(username);
    }

    @Override
    public void logout(String username) {
        viewAssembler.requireUser(username);
        jdbcTemplate.update("update users set status = '0' where username = ?", username);
    }
}
