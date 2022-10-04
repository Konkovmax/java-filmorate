package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Validated
public class Review {
    private int reviewId;

    @NotNull
    private String content;
    private Boolean isPositive;
    private int useful = 0;
    private int userId;
    private int filmId;
    //private Set<Integer> likes = new HashSet<>();


    public Review(int reviewId, String content, Boolean isPositive, int userId, int filmId) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
    }
}
