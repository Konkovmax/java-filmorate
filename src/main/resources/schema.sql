DROP TABLE LIKES if exists;
DROP TABLE FRIENDS if exists;
DROP TABLE FILMS_GENRES if exists;
DROP TABLE GENRES if exists;
drop table FILMS if exists;
DROP TABLE RATINGS if exists;
DROP  TABLE USERS if exists;
CREATE TABLE IF NOT EXISTS
    "USERS" (
                "USERID" INT   NOT NULL AUTO_INCREMENT,
                "NAME" VARCHAR,
                "LOGIN" VARCHAR   NOT NULL,
                "BIRTHDAY" DATE,
                "EMAIL" VARCHAR   NOT NULL,
                CONSTRAINT "PK_USERS" PRIMARY KEY (
                                                   "USERID"
                    )
);

CREATE TABLE IF NOT EXISTS "FRIENDS" (
                                         "USERID" INT   NOT NULL,
                                         "FRIENDID" INT   NOT NULL,
                                         "STATUS" BOOLEAN,
                                         CONSTRAINT "PK_FRIENDS" PRIMARY KEY (
                                         "USERID","FRIENDID"
)
/*    CONSTRAINT "FK_FRIENDS_USERID" FOREIGN KEY("USERID")
    REFERENCES "USERS" ("USERID"),
    CONSTRAINT "FK_FRIENDS_FRIENDID" FOREIGN KEY("FRIENDID")
    REFERENCES "USERS" ("USERID")
  */  );

CREATE TABLE IF NOT EXISTS "FILMS" (
                                       "FILMID" INT   NOT NULL AUTO_INCREMENT,
                                       "NAME" VARCHAR   NOT NULL,
                                       "DESCRIPTION" VARCHAR(200),
    "DURATION" INT,
    "RELEASEDATE" DATE,
    "RATING" INT   NOT NULL,
    CONSTRAINT "PK_FILMS" PRIMARY KEY (
    "FILMID"
                                      )
    --CONSTRAINT "FK_FILMS_RATING" FOREIGN KEY("RATING")
    --REFERENCES "RATINGS" ("RATINGID")
    );

CREATE TABLE IF NOT EXISTS "GENRES" (
                                        "GENREID" INT   NOT NULL,
                                        "GENRE" VARCHAR   NOT NULL,
                                        CONSTRAINT "PK_GENRES" PRIMARY KEY (
                                        "GENREID"
)
    );

CREATE TABLE IF NOT EXISTS "RATINGS" (
                                         "RATINGID" INT   NOT NULL,
                                         "RATING" VARCHAR   NOT NULL,
                                         CONSTRAINT "PK_RATINGS" PRIMARY KEY (
                                         "RATINGID"
)
    );

CREATE TABLE IF NOT EXISTS "LIKES" (
                                       "FILMID" INT   NOT NULL,
                                       "USERSID" INT   NOT NULL,
                                       CONSTRAINT "PK_LIKES" PRIMARY KEY (
                                       "FILMID","USERSID"
)
    /*CONSTRAINT "FK_LIKES_FILMID" FOREIGN KEY("FILMID")
    REFERENCES "FILMS" ("FILMID"),
    CONSTRAINT "FK_LIKES_USERSID" FOREIGN KEY("USERSID")
    REFERENCES "USERS" ("USERID")
    */);

CREATE TABLE IF NOT EXISTS "FILMS_GENRES" (
                                              "GENREID" INT   NOT NULL,
                                              "FILMID" INT   NOT NULL,
                                              CONSTRAINT "PK_FILMS_GENRES" PRIMARY KEY (
                                              "GENREID","FILMID"
)
    /*CONSTRAINT "FK_FILMS_GENRES_GENREID" FOREIGN KEY("GENREID")
    REFERENCES "GENRES" ("GENREID"),
CONSTRAINT "FK_FILMS_GENRES_FILMID" FOREIGN KEY("FILMID")
    REFERENCES "FILMS" ("FILMID")
    */);

ALTER TABLE "FRIENDS" ADD CONSTRAINT "FK_FRIENDS_USERID" FOREIGN KEY("USERID")
    REFERENCES "USERS" ("USERID");

ALTER TABLE "FRIENDS" ADD CONSTRAINT "FK_FRIENDS_FRIENDID" FOREIGN KEY("FRIENDID")
    REFERENCES "USERS" ("USERID");

ALTER TABLE "FILMS" ADD CONSTRAINT "FK_FILMS_RATING" FOREIGN KEY("RATING")
    REFERENCES "RATINGS" ("RATINGID");

ALTER TABLE "LIKES" ADD CONSTRAINT "FK_LIKES_FILMID" FOREIGN KEY("FILMID")
    REFERENCES "FILMS" ("FILMID");

ALTER TABLE "LIKES" ADD CONSTRAINT "FK_LIKES_USERSID" FOREIGN KEY("USERSID")
    REFERENCES "USERS" ("USERID");

ALTER TABLE "FILMS_GENRES" ADD CONSTRAINT "FK_FILMS_GENRES_GENREID" FOREIGN KEY("GENREID")
    REFERENCES "GENRES" ("GENREID");

ALTER TABLE "FILMS_GENRES" ADD CONSTRAINT "FK_FILMS_GENRES_FILMID" FOREIGN KEY("FILMID")
    REFERENCES "FILMS" ("FILMID");
