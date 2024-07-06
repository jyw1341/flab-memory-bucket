INSERT INTO Member (username, email, profile_url)
VALUES ('user1', 'user1@example.com', 'http://example.com/profiles/user1.jpg');

INSERT INTO Album (title, member_id, description, thumbnail_url)
VALUES ('Album 1', 1, 'Description for Album 1', 'http://example.com/thumbnails/1.jpg');

INSERT INTO Series (album_id, name)
VALUES (1, 'Series 1-1'),
       (1, 'Series 1-2');

