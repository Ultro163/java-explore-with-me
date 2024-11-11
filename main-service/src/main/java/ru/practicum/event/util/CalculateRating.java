package ru.practicum.event.util;

import org.springframework.stereotype.Component;
import ru.practicum.event.model.EventLike;

import java.util.List;

@Component
public class CalculateRating {

    public static Double calculateRating(List<EventLike> likes) {
        if (likes == null || likes.isEmpty()) {
            return 0.0;
        }

        long likeCount = likes.stream()
                .filter(l -> "like".equalsIgnoreCase(String.valueOf(l.getReaction()))).count();
        long dislikeCount = likes.stream()
                .filter(l -> "dislike".equalsIgnoreCase(String.valueOf(l.getReaction()))).count();
        long total = likeCount + dislikeCount;

        double rating = total == 0 ? 0.0 : 10.0 * likeCount / total;

        return Math.round(rating * 10) / 10.0;
    }
}