INSERT INTO Member (username, email, profile_url)
VALUES ('user1', 'user1@example.com', 'http://example.com/profiles/user1.jpg'),
       ('user2', 'user2@example.com', 'http://example.com/profiles/user2.jpg');

INSERT INTO Album (title, member_id, description, thumbnail_url)
VALUES ('Album 1', 1, 'Description for Album 1', 'http://example.com/thumbnails/1.jpg'),
       ('Album 2', 1, 'Description for Album 2', 'http://example.com/thumbnails/2.jpg'),
       ('Album 3', 2, 'Description for Album 3', 'http://example.com/thumbnails/3.jpg'),
       ('Album 4', 2, 'Description for Album 4', 'http://example.com/thumbnails/4.jpg');

INSERT INTO Series (album_id, name)
VALUES (1, 'Series 1'),
       (1, 'Series 2'),
       (2, 'Series 3'),
       (2, 'Series 4');


