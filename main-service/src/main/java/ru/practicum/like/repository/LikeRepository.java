package ru.practicum.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.like.dto.RatingDto;
import ru.practicum.like.model.EventLike;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<EventLike, Long> {
    Optional<EventLike> findByUserIdAndEventId(long userId, long eventId);

    void deleteByUserIdAndEventId(long userId, long eventId);

    @Query("""
            SELECT new ru.practicum.like.dto.RatingDto(u.id, el)
            FROM User u
            LEFT JOIN Event e ON u.id = e.initiator.id
            LEFT JOIN EventLike el ON e.id = el.event.id
            WHERE u.id IN :userIds
            """)
    List<RatingDto> findLikes(List<Long> userIds);
}