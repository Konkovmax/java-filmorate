INSERT INTO MPA(MPAID, MPA)
VALUES ( 1, 'G'), ( 2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');
INSERT INTO GENRES(GENREID, GENRE)
    VALUES ( 1, 'Комедия'), ( 2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');
INSERT INTO USERS (name, login, birthday, email)
VALUES ( 'Mario', 'Super', '1989-01-05', 'mario@super.com'),
 ( 'Bon', 'Jovi', '1989-01-05', 'bon@jovi.com'),
( 'User1', 'UserLogin1', '1989-01-05', 'user1@mail.ru'),
 ( 'Ivan', 'Ivanov', '1989-01-05', 'ivan@ivanov.ru');

INSERT INTO FILMS (name, description, duration, releasedate, mpaid)
VALUES ( 'Mario', 'Super', 100, '1989-01-05', 1),
 ( 'Bon', 'Jovi', 90, '1989-01-05', 2),
 ( 'Film1', 'Description1', 300, '1989-03-15', 3),
 ( 'Film2', 'Description2', 25, '2013-03-15', 2),
 ( 'Film3', 'Description3', 45, '2010-06-15', 1),
 ( 'Ivan', 'Ivanov', 80, '1989-01-05', 1);
INSERT INTO FRIENDS(userid, friendid)
VALUES ( 1,2),(1,3),(2,1),(2,3);

INSERT INTO FILMS_GENRES (GENREID, FILMID)
VALUES ( 1,2),(1,3),(2,1),(2,3);

insert into LIKES(filmid, usersid) VALUES ( 2,1 ),(2,2),(3,1),(3,3),(1,4),(2,4),(3,4),(4,4),
                                          (5,1),(5,2),(5,3);


select f.*, r.MPA as mpaName, count(l.USERSID)
from FILMS as f
         left join LIKES as l
                         on f.filmId = l.FILMID
         join MPA R on R.MPAID = f.MPAID
         JOIN FILMS_GENRES FG on f.FILMID = FG.FILMID
WHERE YEAR(f.RELEASEDATE) = 2010
GROUP BY f.FILMID, l.USERSID
order by count(l.USERSID) desc;

select F.*, M.MPA
from LIKES L
join FILMS F on L.FILMID = F.FILMID
join MPA M on F.MPAID = M.MPAID
where YEAR(F.RELEASEDATE) = 2010
group by L.FILMID
order by COUNT(L.USERSID) desc
Limit 3;