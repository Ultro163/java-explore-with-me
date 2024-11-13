package ru.practicum.like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.error.exception.InvalidStateException;
import ru.practicum.error.exception.ValidationException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.like.model.EventLike;
import ru.practicum.like.model.EventReaction;
import ru.practicum.like.repository.LikeRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public void evaluationForEventByUser(long userId, long eventId, String reaction) {
        log.info("Evaluating for event by id: {}, userId= {}, reaction={}", eventId, userId, reaction);
        EventReaction eventReaction = EventReaction.fromString(reaction);
        Event event = findEventById(eventId);
        if (event.getState() != State.PUBLISHED) {
            throw new InvalidStateException("Event is not published");
        }
        User user = checkUserExist(userId);
        EventLike existingEventLike = likeRepository.findByUserIdAndEventId(userId, eventId).orElse(null);
        if (existingEventLike != null) {
            throw new ValidationException("You reaction is already");
        }
        EventLike eventLike = new EventLike();
        eventLike.setUser(user);
        eventLike.setEvent(event);
        eventLike.setReaction(eventReaction);
        likeRepository.save(eventLike);

        log.info("Evaluating for event has been made");
    }

    @Override
    public void updateEvaluationForEventByUser(long userId, long eventId, String reaction) {
        log.info("Update reaction for event by id: {}, userId= {}, reaction={}", eventId, userId, reaction);
        EventReaction eventReaction = EventReaction.fromString(reaction);
        EventLike existingEventLike = checkExistReaction(userId, eventId);
        if (existingEventLike.getReaction() == eventReaction) {
            throw new ValidationException("You can't change the reaction, the reaction is already " + reaction);
        }
        existingEventLike.setReaction(eventReaction);
        likeRepository.save(existingEventLike);
        log.info("Updated reaction for event by id: {}, userId= {}, reaction={}", eventId, userId, reaction);
    }

    @Override
    public void deleteEvaluationForEventByUser(long userId, long eventId) {
        log.info("Delete reaction userId={} for eventId={}", userId, eventId);
        likeRepository.deleteByUserIdAndEventId(userId, eventId);
    }

    private EventLike checkExistReaction(long userId, long eventId) {
        return likeRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> {
                    log.warn("Reaction with userId {} for eventId={} not found", userId, eventId);
                    return new EntityNotFoundException("Reaction with userId=" + userId + "for eventId="
                            + eventId + " not found");
                });

    }

    private User checkUserExist(long userId) {
        log.info("Getting user with ID = {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", userId);
                    return new EntityNotFoundException("User with ID " + userId + " not found");
                });
    }

    private Event findEventById(long eventId) {
        return eventRepository.findByIdFetch(eventId).orElseThrow(() -> {
            log.warn("EventId={} not found", eventId);
            return new EntityNotFoundException("Event with id " + eventId + " not found");
        });
    }
}