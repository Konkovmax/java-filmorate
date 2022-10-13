package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationDBStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmStorage;

    public List<Film> recommendations(int userId) {
        List<Film> recommendations = new ArrayList<>();
        String createQuery = "select FILMID from LIKES Where USERSID = ? ";
        List<Integer> filmsForUserId = jdbcTemplate.queryForList(createQuery, Integer.class, userId);
        if (filmsForUserId.size()<1) {
            return recommendations;
        }
        createQuery = "select USERSID from(select USERSID, COUNT(FILMID) as commonFilm " +
                "from LIKES " +
                "WHERE USERSID != ? " +
                "AND FILMID IN (select FILMID from LIKES Where USERSID = ?) " +
                "GROUP BY USERSID " +
                "ORDER BY commonFilm DESC " +
                "LIMIT 1) ";

        List <Integer> otherUserIds = jdbcTemplate.queryForList(createQuery, Integer.class, userId, userId);
        if (otherUserIds.size()<1) {
          throw new NotFoundException("Пользователь с похожими лайками фильмов не найден");
        }
        int otherUserId = otherUserIds.get(0);

        String createQueryRecommendation = "select FILMID from LIKES " +
                "WHERE USERSID = ? AND " +
                "FILMID NOT IN (select FILMID from LIKES Where USERSID = ?) ";

        List<Integer> filmsId = jdbcTemplate.queryForList(createQueryRecommendation, Integer.class, otherUserId, userId);

        for (int idFilm : filmsId) {
            Optional<Film> film = filmStorage.getFilm(idFilm);
            if (film.isPresent()) {
                recommendations.add(filmStorage.getFilm(idFilm).get());
            }
        }
        return recommendations;
    }
}
