package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validators.ReleaseDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
