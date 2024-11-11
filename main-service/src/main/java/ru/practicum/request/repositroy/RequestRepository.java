package ru.practicum.request.repositroy;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterIdAndEventInitiatorIdNot(long requesterId, long initiatorId);

    Optional<Request> findByRequesterIdAndEventId(long userId, long eventId);

    Optional<Request> findByIdAndRequesterId(long requestId, long userId);

    List<Request> findAllByIdIn(Set<Long> ids);

    List<Request> findByEventId(long eventID);
}