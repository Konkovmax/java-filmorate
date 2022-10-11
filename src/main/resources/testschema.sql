DROP TABLE  IF EXISTS LIKES, FRIENDS, FILMS_GENRES, GENRES, FILMS, RATINGS, MPA, USERS, REVIEWS,
    REVIEW_SCORES, EVENTTYPES, OPERATIONS, EVENTS, EVENT_LIKE, EVENT_REVIEW, EVENT_FRIEND CASCADE;

CREATE TABLE IF NOT EXISTS "USERS"
(
    "USERID"   INT     NOT NULL AUTO_INCREMENT,
    "NAME"     VARCHAR,
    "LOGIN"    VARCHAR NOT NULL,
    "BIRTHDAY" DATE,
    "EMAIL"    VARCHAR NOT NULL,
    CONSTRAINT "PK_USERS" PRIMARY KEY ("USERID")
);

CREATE TABLE IF NOT EXISTS "FRIENDS"
(
    "USERID"   INT NOT NULL,
    "FRIENDID" INT NOT NULL,
    "STATUS"   BOOLEAN,
    CONSTRAINT "PK_FRIENDS" PRIMARY KEY ("USERID", "FRIENDID")
);

CREATE TABLE IF NOT EXISTS "FILMS"
(
    "FILMID"      INT     NOT NULL AUTO_INCREMENT,
    "NAME"        VARCHAR NOT NULL,
    "DESCRIPTION" VARCHAR(200),
    "DURATION"    INT,
    "RELEASEDATE" DATE,
    "MPAID"      INT     NOT NULL,
    "DIRECTORID" INT,
    CONSTRAINT "PK_FILMS" PRIMARY KEY ("FILMID")
);

CREATE TABLE IF NOT EXISTS "GENRES"
(
    "GENREID" INT     NOT NULL,
    "GENRE"   VARCHAR NOT NULL,
    CONSTRAINT "PK_GENRES" PRIMARY KEY ("GENREID")
);

CREATE TABLE IF NOT EXISTS "MPA"
(
    "MPAID" INT     NOT NULL,
    "MPA"   VARCHAR NOT NULL,
    CONSTRAINT "PK_RATINGS" PRIMARY KEY ("MPAID")
);

CREATE TABLE IF NOT EXISTS "LIKES"
(
    "FILMID"  INT NOT NULL,
    "USERSID" INT NOT NULL,
    CONSTRAINT "PK_LIKES" PRIMARY KEY ("FILMID", "USERSID")
);

CREATE TABLE IF NOT EXISTS "FILMS_GENRES"
(
    "GENREID" INT NOT NULL,
    "FILMID"  INT NOT NULL,
    CONSTRAINT "PK_FILMS_GENRES" PRIMARY KEY ("GENREID", "FILMID")
);

CREATE TABLE IF NOT EXISTS "DIRECTOR"
(
    "DIRECTORID" INT     NOT NULL AUTO_INCREMENT,
    "NAME"  VARCHAR,
    CONSTRAINT "PK_DIRECTORID" PRIMARY KEY ("DIRECTORID")
    );

ALTER TABLE "FRIENDS"
    ADD CONSTRAINT "FK_FRIENDS_USERID" FOREIGN KEY ("USERID")
        REFERENCES "USERS" ("USERID");

ALTER TABLE "FRIENDS"
    ADD CONSTRAINT "FK_FRIENDS_FRIENDID" FOREIGN KEY ("FRIENDID")
        REFERENCES "USERS" ("USERID");

ALTER TABLE "FILMS"
    ADD CONSTRAINT "FK_FILMS_RATING" FOREIGN KEY ("MPAID")
        REFERENCES "MPA" ("MPAID");

ALTER TABLE "LIKES"
    ADD CONSTRAINT "FK_LIKES_FILMID" FOREIGN KEY ("FILMID")
        REFERENCES "FILMS" ("FILMID");

ALTER TABLE "LIKES"
    ADD CONSTRAINT "FK_LIKES_USERSID" FOREIGN KEY ("USERSID")
        REFERENCES "USERS" ("USERID");

ALTER TABLE "FILMS_GENRES"
    ADD CONSTRAINT "FK_FILMS_GENRES_GENREID" FOREIGN KEY ("GENREID")
        REFERENCES "GENRES" ("GENREID");

ALTER TABLE "FILMS_GENRES"
    ADD CONSTRAINT "FK_FILMS_GENRES_FILMID" FOREIGN KEY ("FILMID")
        REFERENCES "FILMS" ("FILMID");
