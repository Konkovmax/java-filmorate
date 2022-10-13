package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storages.RecommendationDBStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationDBStorage recommendationStorage;

    public List<Film> recommendations(int userId) {
        return recommendationStorage.recommendations(userId);
    }
}
