package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validators.ReleaseDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.FilmorateApplication.DATE_FORMATTER;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Validated
public class Film {
    private int id;

    @NotEmpty
    private String name;

    @Size(max = 200)
    private String description;

    @ReleaseDate
    private LocalDate releaseDate;

    @Min(1)
    private int duration;
    private Set<Integer> likes = new HashSet<>();
    private List<Genre> genres = new ArrayList<>();

    @NotNull
    private Mpa mpa;

    private List<Director> directors;

    private Director director;

    public Film(int id, String name, String description, String releaseDate, int duration, int ratingId, String ratingName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate, DATE_FORMATTER);
        this.duration = duration;
        this.mpa = new Mpa(ratingId, null);
        this.mpa.setName(ratingName);
    }

    public void addLike(int likeId) {
        likes.add(likeId);
    }

    public void removeLike(int likeId) {
        likes.remove(likeId);
    }

    public Set<Integer> getLikes() {
        return likes;
    }
}
