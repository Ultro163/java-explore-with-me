package ru.practicum.request.repositroy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("""
            select r
            from Request r
            left join fetch r.event
            where r.id != :userId
            order by r.created
            """)
    List<Request> findAllByInitiatorIdNot(long userId);

    Optional<Request> findByRequesterIdAndEventId(long userId, long eventId);

    Optional<Request> findByIdAndRequesterId(long requestId, long userId);
}