package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.model.EventLike;

import java.util.List;

public interface EventLikeRepository extends JpaRepository<EventLike, Long> {
    EventLike findByUserIdAndEventId(long userId, long eventId);

    @Query("""
            select el
            from EventLike el
            where el.event.id in :eventIds
            """)
    List<EventLike> findAllForUser(List<Long> eventIds);
}