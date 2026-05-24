package com.example.gallerybackend.service.impl;

import com.example.gallerybackend.service.UserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final JdbcTemplate jdbcTemplate;
    private final ViewAssembler viewAssembler;

    public UserServiceImpl(JdbcTemplate jdbcTemplate, ViewAssembler viewAssembler) {
        this.jdbcTemplate = jdbcTemplate;
        this.viewAssembler = viewAssembler;
    }

    @Override
    public List<Map<String, Object>> listUsers() {
        List<Map<String, Object>> users = jdbcTemplate.queryForList("select username from users order by id");
        return users.stream().map(row -> viewAssembler.buildUserView(String.valueOf(row.get("username")))).toList();
    }

    @Override
    public Map<String, Object> getUserByUsername(String username) {
        return viewAssembler.buildUserView(username);
    }

    @Override
    public Map<String, Object> updatePermission(String username, String permission) {
        viewAssembler.requireUser(username);
        jdbcTemplate.update("update users set gallery_permission = ? where username = ?", permission, username);
        return viewAssembler.buildUserView(username);
    }

    @Override
    @Transactional
    public Map<String, Object> addFriend(String username, String friendUsername) {
        Map<String, Object> user = viewAssembler.requireUser(username);
        Map<String, Object> friend = viewAssembler.requireUser(friendUsername);
        Long userId = ((Number) user.get("id")).longValue();
        Long friendId = ((Number) friend.get("id")).longValue();
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("cannot add yourself as a friend");
        }
        addFriendIfAbsent(userId, friendId);
        addFriendIfAbsent(friendId, userId);
        return viewAssembler.buildUserView(username);
    }

    @Override
    @Transactional
    public Map<String, Object> removeFriend(String username, String friendUsername) {
        Map<String, Object> user = viewAssembler.requireUser(username);
        Map<String, Object> friend = viewAssembler.requireUser(friendUsername);
        Long userId = ((Number) user.get("id")).longValue();
        Long friendId = ((Number) friend.get("id")).longValue();
        jdbcTemplate.update("delete from friendships where user_id = ? and friend_id = ?", userId, friendId);
        jdbcTemplate.update("delete from friendships where user_id = ? and friend_id = ?", friendId, userId);
        return viewAssembler.buildUserView(username);
    }

    private void addFriendIfAbsent(Long userId, Long friendId) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from friendships where user_id = ? and friend_id = ?",
                Integer.class,
                userId,
                friendId
        );
        if (count != null && count == 0) {
            jdbcTemplate.update("insert into friendships(user_id, friend_id) values(?, ?)", userId, friendId);
        }
    }
}
