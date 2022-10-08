INSERT INTO MPA(MPAID, MPA)
VALUES ( 1, 'G'), ( 2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');
INSERT INTO GENRES(GENREID, GENRE)
    VALUES ( 1, 'Комедия'), ( 2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');
INSERT INTO USERS (name, login, birthday, email)
VALUES ( 'Mario', 'Super', '1989-01-05', 'mario@super.com'),
 ( 'Bon', 'Jovi', '1989-01-05', 'bon@jovi.com'),
 ( 'Ivan', 'Ivanov', '1989-01-05', 'ivan@ivanov.ru');

INSERT INTO FILMS (name, description, duration, releasedate, mpaid)
VALUES ( 'Mario', 'Super', 100, '1989-01-05', 1),
 ( 'Bon', 'Jovi', 90, '1989-01-05', 2),
 ( 'Ivan', 'Ivanov', 80, '1989-01-05', 3);
INSERT INTO FRIENDS(userid, friendid)
VALUES ( 1,2),(1,3),(2,1),(2,3);

insert into LIKES(filmid, usersid) VALUES ( 2,1 ),(2,2),(3,1),(3,3);


INSERT INTO REVIEWS (FILMID, USERID, CONTENT, ISPOSITIVE) VALUES ( 1, 1, 'REVIEW-1', TRUE );
INSERT INTO EVENTTYPES(EVENTTYPEID, EVENTTYPE) VALUES (1, 'LIKE'), (2, 'REVIEW'), (3, 'FRIEND');
INSERT INTO OPERATIONS(OPERATIONID, OPERATION) VALUES (1, 'REMOVE'), (2, 'ADD'), (3, 'UPDATE');

INSERT INTO EVENTS (USERID, EVENTTYPEID, OPERATIONID) VALUES (1, 1, 1);
INSERT INTO EVENT_LIKE (EVENTID, FILMID) VALUES (1, 1);

INSERT INTO EVENTS (USERID, EVENTTYPEID, OPERATIONID) VALUES (2, 2, 3);
INSERT INTO EVENT_REVIEW (EVENTID, REVIEWID) VALUES (2, 1);

INSERT INTO EVENTS (USERID, EVENTTYPEID, OPERATIONID) VALUES (3, 3, 2);
INSERT INTO EVENT_FRIEND (EVENTID, FRIENDID) VALUES (3, 2);
