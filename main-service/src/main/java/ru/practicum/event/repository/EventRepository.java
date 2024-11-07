package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findAllByInitiatorId(long userId, Pageable pageable);

    Event findByIdAndInitiatorId(long id, long userId);

    List<Event> findAllByIdIn(Set<Long> ids);
}