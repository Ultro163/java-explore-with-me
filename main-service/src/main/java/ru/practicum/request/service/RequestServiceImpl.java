package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.exception.DuplicateParticipationRequestException;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.error.exception.InvalidStateException;
import ru.practicum.error.exception.SelfParticipationException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestState;
import ru.practicum.request.repositroy.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserService userServiceImpl;

    @Override
    public Request addRequest(long userId, long eventId) {
        log.info("Adding request for eventId={} from userId={}", eventId, userId);
        User user = checkUserExist(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with ID={} not found", eventId);
                    return new EntityNotFoundException("Event with ID=" + eventId + " not found");
                });
        if (userId == event.getInitiator().getId()) {
            log.warn("A user with ID={} cannot add a request to their event", userId);
            throw new SelfParticipationException("You cannot add a request to your event");
        }
        checkParticipationRequestExists(userId, eventId);
        if (event.getState() != State.PUBLISHED) {
            log.warn("Event with ID={} has not published", eventId);
            throw new InvalidStateException("Event has not published");
        }
        int quantityParticipantWithNewRequest = event.getConfirmedRequests() + 1;

        if (event.getParticipantLimit() != 0
                && quantityParticipantWithNewRequest > event.getParticipantLimit()) {
            log.warn("Event with ID={} has reached the full number of participants", eventId);
            throw new InvalidStateException("The event has reached the full number of participants");
        }
        Request newRequest = new Request();
        newRequest.setRequester(user);
        newRequest.setEvent(event);
        newRequest.setCreated(LocalDateTime.now());
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestState.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            newRequest.setStatus(RequestState.PENDING);
        }
        Request savedRequest = requestRepository.save(newRequest);
        eventRepository.save(event);
        log.info("Added request={} ", savedRequest);
        return savedRequest;
    }

    private void checkParticipationRequestExists(long userId, long eventId) {
        requestRepository.findByRequesterIdAndEventId(userId, eventId)
                .ifPresent(request -> {
                    log.warn("Request by userId={} already exists", userId);
                    throw new DuplicateParticipationRequestException("Request already exists");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> getRequests(long userId) {
        log.info("Getting requests from userId={}", userId);
        checkUserExist(userId);
        List<Request> requests = requestRepository.findAllByRequesterIdAndEventInitiatorIdNot(userId, userId);
        if (requests == null) {
            return Collections.emptyList();
        }
        return requests;
    }

    @Override
    public Request updateRequest(long userId, long requestId) {
        log.info("Updating request with id={} from userId={}", requestId, userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> {
                    log.warn("Request with id={}, from userId= {} not found", requestId, userId);
                    return new EntityNotFoundException("Request with id=" + requestId +
                            " from userId=" + userId + " not found");
                });
        request.setStatus(RequestState.CANCELED);
        return requestRepository.save(request);
    }

    private User checkUserExist(long userId) {
        return userServiceImpl.getUserById(userId);
    }
}