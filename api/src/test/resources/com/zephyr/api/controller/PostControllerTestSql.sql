INSERT INTO Member (username, email, profile_url)
VALUES ('user1', 'user1@example.com', 'http://example.com/profiles/user1.jpg'),
       ('user2', 'user2@example.com', 'http://example.com/profiles/user2.jpg'),
       ('user3', 'user3@example.com', 'http://example.com/profiles/user3.jpg');

INSERT INTO Album (title, member_id, description, thumbnail_url)
VALUES ('Album 1-1', 1, 'Description for Album 1-1', 'http://example.com/thumbnails/1-1.jpg'),
       ('Album 1-2', 1, 'Description for Album 1-2', 'http://example.com/thumbnails/1-2.jpg'),
       ('Album 1-3', 1, 'Description for Album 1-3', 'http://example.com/thumbnails/1-3.jpg'),
       ('Album 2-1', 2, 'Description for Album 2-1', 'http://example.com/thumbnails/2-1.jpg'),
       ('Album 2-2', 2, 'Description for Album 2-2', 'http://example.com/thumbnails/2-2.jpg'),
       ('Album 2-3', 2, 'Description for Album 2-3', 'http://example.com/thumbnails/2-3.jpg'),
       ('Album 3-1', 3, 'Description for Album 3-1', 'http://example.com/thumbnails/3-1.jpg'),
       ('Album 3-2', 3, 'Description for Album 3-2', 'http://example.com/thumbnails/3-2.jpg'),
       ('Album 3-3', 3, 'Description for Album 3-3', 'http://example.com/thumbnails/3-3.jpg');

INSERT INTO Series (album_id, name, post_count, first_date, last_date, thumbnail_url)
VALUES (1, 'Series 1-1', 0, NULL, NULL, NULL),
       (1, 'Series 1-2', 0, NULL, NULL, NULL),
       (2, 'Series 2-1', 0, NULL, NULL, NULL),
       (2, 'Series 2-2', 0, NULL, NULL, NULL),
       (3, 'Series 3-1', 0, NULL, NULL, NULL),
       (3, 'Series 3-2', 0, NULL, NULL, NULL);




