package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.EventLike;

public interface EventLikeRepository extends JpaRepository<EventLike, Long> {
    EventLike findByUserIdAndEventId(long userId, long eventId);
}