package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Review {
    private int reviewId;
    private String content;
    private Boolean isPositive;
    private int useful = 0;
    private Set<Integer> likes = new HashSet<>();
}
