CREATE DATABASE IF NOT EXISTS gallery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gallery_db;

DROP TABLE IF EXISTS wall_placements;
DROP TABLE IF EXISTS friendships;
DROP TABLE IF EXISTS painting_comments;
DROP TABLE IF EXISTS music;
DROP TABLE IF EXISTS paintings;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    status CHAR(1) NOT NULL DEFAULT '0',
    gallery_permission VARCHAR(16) NOT NULL DEFAULT 'public'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    nickname VARCHAR(64) DEFAULT NULL,
    status CHAR(1) NOT NULL DEFAULT '0',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE paintings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    img_url VARCHAR(255) NOT NULL,
    author VARCHAR(128) NOT NULL,
    create_time VARCHAR(32) NOT NULL,
    type VARCHAR(32) NOT NULL,
    status CHAR(1) NOT NULL DEFAULT '0',
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE painting_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    painting_id BIGINT NOT NULL,
    commentator VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,
    create_time DATETIME NOT NULL,
    CONSTRAINT fk_comment_painting FOREIGN KEY (painting_id) REFERENCES paintings(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE music (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    singer VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE friendships (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    UNIQUE KEY uk_friendship (user_id, friend_id),
    CONSTRAINT fk_friendship_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friendship_friend FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE wall_placements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    painting_id BIGINT NOT NULL,
    wall_name VARCHAR(16) NOT NULL,
    position_x DECIMAL(10,2) NOT NULL DEFAULT 0,
    position_y DECIMAL(10,2) NOT NULL DEFAULT 0,
    position_z DECIMAL(10,2) NOT NULL DEFAULT 0,
    scale_x DECIMAL(10,2) NOT NULL DEFAULT 1,
    scale_y DECIMAL(10,2) NOT NULL DEFAULT 1,
    scale_z DECIMAL(10,2) NOT NULL DEFAULT 1,
    size_width DECIMAL(10,2) NOT NULL DEFAULT 4,
    size_height DECIMAL(10,2) NOT NULL DEFAULT 3,
    UNIQUE KEY uk_user_painting (user_id, painting_id),
    CONSTRAINT fk_wall_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_wall_painting FOREIGN KEY (painting_id) REFERENCES paintings(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (id, username, password, status, gallery_permission) VALUES
(1, 'admin1', '123456', '0', 'public'),
(2, 'admin2', '123456', '0', 'public'),
(3, 'admin3', '123456', '0', 'private'),
(4, 'admin4', '123456', '0', 'friends');

INSERT INTO admins (id, username, password, nickname, status) VALUES
(1, 'admin', '123456', '系统管理员', '0');

INSERT INTO paintings (id, name, img_url, author, create_time, type, status, like_count, comment_count) VALUES
(1, 'Sunrise', 'img/sunrise.jpg', 'Monet', '1872', 'impression', '0', 0, 0),
(2, 'Castle by Lake', 'img/2.jpg', 'Monet', '1916', 'impression', '0', 0, 0),
(3, 'Seaside Cliff', 'img/3.jpg', 'Monet', '1889', 'impression', '0', 0, 0),
(4, 'Seascape', 'img/4.jpg', 'Monet', '1888', 'impression', '0', 0, 0),
(5, 'Starry Night', 'img/5.jpg', 'Van Gogh', '1889', 'impression', '0', 0, 0),
(6, 'Classical Work 1', 'img/6.jpg', 'Classical Author', '1897', 'classical', '0', 0, 0),
(7, 'Classical Work 2', 'img/7.jpg', 'Classical Author', '1898', 'classical', '0', 0, 0),
(8, 'Classical Work 3', 'img/8.jpg', 'Classical Author', '1899', 'classical', '0', 0, 0),
(9, 'Classical Work 4', 'img/9.jpg', 'Classical Author', '1900', 'classical', '0', 0, 0),
(10, 'Monet 10', 'img/10.jpg', 'Monet', '1875', 'impression', '0', 0, 0),
(11, 'Monet 11', 'img/11.jpg', 'Monet', '1876', 'impression', '0', 0, 0),
(12, 'Monet 12', 'img/12.jpg', 'Monet', '1877', 'impression', '0', 0, 0),
(13, 'Monet 13', 'img/13.jpg', 'Monet', '1878', 'impression', '0', 0, 0),
(14, 'Modern Work 1', 'img/14.jpg', 'Modern Artist W', '2000', 'modern', '0', 0, 0),
(15, 'Modern Work 2', 'img/15.jpg', 'Modern Artist W', '2001', 'modern', '0', 0, 0),
(16, 'Modern Work 3', 'img/16.jpg', 'Modern Artist W', '2002', 'modern', '0', 0, 0),
(17, 'Modern Work 4', 'img/17.jpg', 'Modern Artist W', '2003', 'modern', '0', 0, 0),
(18, 'Modern Work 5', 'img/18.jpg', 'Modern Artist W', '2004', 'modern', '0', 0, 0),
(19, 'Modern Work 6', 'img/19.jpg', 'Modern Artist W', '2005', 'modern', '0', 0, 0),
(20, 'Rei 1', 'img/20.jpg', 'Lei', '2010', 'modern', '0', 0, 0),
(21, 'Rei 2', 'img/21.jpg', 'Lei', '2011', 'modern', '0', 0, 0),
(22, 'Rei 3', 'img/22.jpg', 'Lei', '2012', 'modern', '0', 0, 0),
(23, 'Rei 4', 'img/23.jpg', 'Lei', '2013', 'modern', '0', 0, 0),
(24, 'Rei 5', 'img/24.jpg', 'Lei', '2014', 'modern', '0', 0, 0);

INSERT INTO music (id, name, singer, url) VALUES
(1, 'Track 1', 'Singer 1', 'music/track1.mp3'),
(2, 'Track 2', 'Singer 2', 'music/track2.mp3'),
(3, 'Track 3', 'Singer 3', 'music/track3.mp3'),
(4, 'Track 4', 'Singer 4', 'music/track4.mp3'),
(5, 'Track 5', 'Singer 5', 'music/track5.mp3'),
(6, 'Track 6', 'Singer 6', 'music/track6.mp3');

ALTER TABLE users AUTO_INCREMENT = 5;
ALTER TABLE admins AUTO_INCREMENT = 2;
ALTER TABLE paintings AUTO_INCREMENT = 25;
ALTER TABLE music AUTO_INCREMENT = 7;
