INSERT INTO mpa(mpaid, mpa)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');
INSERT INTO genres(genreid, genre)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');
INSERT INTO users (name, login, birthday, email)
VALUES ('Mario', 'Super', '1989-01-05', 'mario@super.com'),
       ('Bon', 'Jovi', '1989-01-05', 'bon@jovi.com'),
       ('Ivan', 'Ivanov', '1989-01-05', 'ivan@ivanov.ru');

INSERT INTO films (name, description, duration, releasedate, mpaid)
VALUES ('Mario', 'Super', 100, '1989-01-05', 1),
       ('Bon', 'Jovi', 90, '1989-01-05', 2),
       ('Ivan', 'Ivanov', 80, '1989-01-05', 3);
INSERT INTO friends(userid, friendid)
VALUES (1, 2),
       (1, 3),
       (2, 1),
       (2, 3);

INSERT INTO likes(filmid, usersid)
VALUES (2, 1),
       (2, 2),
       (3, 1),
       (3, 3);


INSERT INTO reviews (filmid, userid, content, ispositive)
VALUES (1, 1, 'REVIEW-1', TRUE);
INSERT INTO eventtypes(eventtypeid, eventtype)
VALUES (1, 'LIKE'),
       (2, 'REVIEW'),
       (3, 'FRIEND');
INSERT INTO operations(operationid, operation)
VALUES (1, 'REMOVE'),
       (2, 'ADD'),
       (3, 'UPDATE');

INSERT INTO events (userid, eventtypeid, operationid)
VALUES (1, 1, 1);
INSERT INTO event_like (eventid, filmid)
VALUES (1, 1);

INSERT INTO events (userid, eventtypeid, operationid)
VALUES (2, 2, 3);
INSERT INTO event_review (eventid, reviewid)
VALUES (2, 1);

INSERT INTO events (userid, eventtypeid, operationid)
VALUES (3, 3, 2);
INSERT INTO event_friend (eventid, friendid)
VALUES (3, 2);
