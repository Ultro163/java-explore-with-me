package ru.practicum.event.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.NonNull;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @EntityGraph(attributePaths = {"category", "initiator", "location", "likes"})
    List<Event> findAllByInitiatorId(long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "initiator", "location", "likes"})
    Event findByIdAndInitiatorId(long id, long userId);

    List<Event> findAllByIdIn(Set<Long> ids);

    @NonNull
    @EntityGraph(attributePaths = {"category", "initiator", "location", "likes"})
    Page<Event> findAll(@NonNull Predicate predicate, @NonNull Pageable pageable);

    @NonNull
    @EntityGraph(attributePaths = {"category", "initiator", "location", "likes"})
    Page<Event> findAll(@NonNull Pageable pageable);
}