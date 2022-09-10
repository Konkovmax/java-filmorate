package ru.yandex.practicum.filmorate.model;
//сначала сделал список жаноров, но потом понял, что если данные берутся из базы, то зачем этот список.
// Хотел удалить, но не стал, т.к. у нас интерфес, для разных версий(БД и без БД) и в теории это тоже нужно будет...?

public enum GenreTypes {
    COMEDY,
    DRAMA,
    CARTOON,
    THRILLER,
    DOCUMENTARY,
    ACTION
}
