drop table LIKES;
DROP TABLE Friends;
drop  table USERS;
CREATE TABLE IF NOT EXISTS
    "USERS" (
                "USERID" int   NOT NULL AUTO_INCREMENT,
                "NAME" varchar,
                "LOGIN" varchar   NOT NULL,
                "BIRTHDAY" date,
                "EMAIL" varchar   NOT NULL,
                CONSTRAINT "pk_Users" PRIMARY KEY (
                                                   "USERID"
                    )
);

CREATE TABLE IF NOT EXISTS "FRIENDS" (
                                         "USERID" int   NOT NULL,
                                         "FriendID" int   NOT NULL,
                                         "Status" boolean,
                                         CONSTRAINT "pk_Friends" PRIMARY KEY (
                                         "USERID","FriendID"
),
    CONSTRAINT "fk_Friends_UserID" FOREIGN KEY("USERID")
    REFERENCES "USERS" ("USERID"),
    CONSTRAINT "fk_Friends_FriendID" FOREIGN KEY("FriendID")
    REFERENCES "USERS" ("USERID")
    );

CREATE TABLE IF NOT EXISTS "Films" (
                                       "FilmID" int   NOT NULL,
                                       "Name" varchar   NOT NULL,
                                       "Description" varchar(200),
    "Duration" int,
    "ReleaseDate" date,
    "Rating" int   NOT NULL,
    CONSTRAINT "pk_Films" PRIMARY KEY (
    "FilmID"
                                      ),
    CONSTRAINT "fk_Films_Rating" FOREIGN KEY("Rating")
    REFERENCES "Ratings" ("RatingID")
    );

CREATE TABLE IF NOT EXISTS "Genres" (
                                        "GenreID" int   NOT NULL,
                                        "Genre" varchar   NOT NULL,
                                        CONSTRAINT "pk_Genres" PRIMARY KEY (
                                        "GenreID"
)
    );

CREATE TABLE IF NOT EXISTS "Ratings" (
                                         "RatingID" int   NOT NULL,
                                         "Rating" varchar   NOT NULL,
                                         CONSTRAINT "pk_Ratings" PRIMARY KEY (
                                         "RatingID"
)
    );

CREATE TABLE IF NOT EXISTS "LIKES" (
                                       "FilmID" int   NOT NULL,
                                       "USERSID" int   NOT NULL,
                                       CONSTRAINT "pk_Likes" PRIMARY KEY (
                                       "FilmID","USERSID"
),
    CONSTRAINT "fk_Likes_FilmID" FOREIGN KEY("FilmID")
    REFERENCES "Films" ("FilmID"),
    CONSTRAINT "fk_Likes_UsersID" FOREIGN KEY("USERSID")
    REFERENCES "USERS" ("USERID")
    );

CREATE TABLE IF NOT EXISTS "Films_genres" (
                                              "GenreID" int   NOT NULL,
                                              "FilmID" int   NOT NULL,
                                              CONSTRAINT "pk_Films_genres" PRIMARY KEY (
                                              "GenreID","FilmID"
),
    CONSTRAINT "fk_Films_genres_GenreID" FOREIGN KEY("GenreID")
    REFERENCES "Genres" ("GenreID"),
CONSTRAINT "fk_Films_genres_FilmID" FOREIGN KEY("FilmID")
    REFERENCES "Films" ("FilmID")
    );

/*ALTER TABLE "Friends" ADD CONSTRAINT "fk_Friends_UserID" FOREIGN KEY("UserID")
    REFERENCES "Users" ("UserID");

ALTER TABLE "Friends" ADD CONSTRAINT "fk_Friends_FriendID" FOREIGN KEY("FriendID")
    REFERENCES "Users" ("UserID");

ALTER TABLE "Films" ADD CONSTRAINT "fk_Films_Rating" FOREIGN KEY("Rating")
    REFERENCES "Ratings" ("RatingID");

ALTER TABLE "Likes" ADD CONSTRAINT "fk_Likes_FilmID" FOREIGN KEY("FilmID")
    REFERENCES "Films" ("FilmID");

ALTER TABLE "Likes" ADD CONSTRAINT "fk_Likes_UsersID" FOREIGN KEY("UsersID")
    REFERENCES "Users" ("UserID");

ALTER TABLE "Films_genres" ADD CONSTRAINT "fk_Films_genres_GenreID" FOREIGN KEY("GenreID")
    REFERENCES "Genres" ("GenreID");

ALTER TABLE "Films_genres" ADD CONSTRAINT "fk_Films_genres_FilmID" FOREIGN KEY("FilmID")
    REFERENCES "Films" ("FilmID");
*/