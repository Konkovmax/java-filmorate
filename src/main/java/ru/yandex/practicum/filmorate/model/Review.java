package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Review {
    String content;
    int score;//rating подошло бы лучше, но один рейтинг уже есть, поэтому чтобы не запутаться назвал подругому
    Boolean positive;
}
