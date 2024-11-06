package ru.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.ViewStats;
import ru.practicum.error.exception.AccessDeniedException;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.error.exception.InvalidStateException;
import ru.practicum.error.exception.ValidationException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.dto.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.repositroy.LocationRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.event.model.QEvent.event;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatClient statClient;

    @Override
    public EventFullDto createEvent(NewEventDto eventDto) {
        log.info("Creating event {}", eventDto);
        User user = checkUserExist(eventDto.getInitiator());
        Category category = categoryService.getCategoryById(eventDto.getCategory());
        Location location = locationRepository.save(eventDto.getLocation());
        Event event = eventMapper.toEntity(eventDto);
        event.setCreatedOn(LocalDateTime.now());
        event.setPublishedOn(LocalDateTime.now());
        event.setConfirmedRequests(0);
        event.setState(State.PENDING);
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        Event savedEvent = eventRepository.save(event);
        savedEvent.setViews(0L);
        log.info("Event {} saved", savedEvent);
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventFullDto updateTheEventByTheUser(long userId, long eventId, UpdateEventUserRequest dto) {
        log.info("Updating event from user with Id {} ", eventId);
        User user = checkUserExist(userId);
        Event event = findEventById(eventId);
        if (event.getState() == State.PUBLISHED) {
            log.warn("Event with id {} is already published. Cannot perform this operation.", event.getId());
            throw new InvalidStateException("Event has already been published");
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
                default -> throw new InvalidStateException("Invalid state action");
            }
        }
        if (!user.getId().equals(event.getInitiator().getId())) {
            log.warn("User id {} is the dont same as initiator", userId);
            throw new AccessDeniedException("You do not have permission to update this event");
        }
        Event savedEvent = updateEvent(event, dto.getCategoryId(), dto.getLocation(), dto.getAnnotation(),
                dto.getDescription(), dto.getEventDate(), dto.getPaid(), dto.getParticipantLimit(),
                dto.getRequestModeration(), dto.getTitle());
        setViews(List.of(savedEvent));
        log.info("Event {} updated from user", savedEvent);
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        log.info("Getting events for userId {}, with offset from {}, size {} ", userId, from, size);
        checkUserExist(userId);
        Pageable pageable = createPageable(from / size, size, Sort.by(Sort.Direction.ASC, "createdOn"));
        List<Event> result = eventRepository.findAllByInitiatorId(userId, pageable);
        log.info("Found {} events", result.size());
        setViews(result);
        return result.stream().map(eventMapper::toShortDto).toList();
    }

    @Override
    public EventFullDto getUserFullEventBuId(long userId, long eventId) {
        log.info("Getting full event with Id {} for userId {}", eventId, userId);
        checkUserExist(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            log.warn("Event with id {} for userId {} , not found", eventId, userId);
            throw new EntityNotFoundException("Event with id " + eventId + "for userId " + userId + " not found");
        }

        setViews(List.of(event));

        return eventMapper.toDto(event);
    }

    @Override
    public List<EventFullDto> getFullEventsForAdmin(List<Long> userIds, List<State> states, List<Long> categories,
                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                    int from, int size) {

        log.info("Getting full events for admin with parameters: " +
                        "userIds={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                userIds, states, categories, rangeStart, rangeEnd, from, size);
        Page<Event> eventPage;
        Pageable pageable = createPageable(from, size, Sort.by(Sort.Direction.ASC, "createdOn"));
        BooleanBuilder queryBuilder = new BooleanBuilder();


        applyDateRangeFilter(rangeStart, rangeEnd, queryBuilder);

        if (userIds != null && !userIds.isEmpty()) {
            queryBuilder.and(event.initiator.id.in(userIds));
        }
        if (categories != null && !categories.isEmpty()) {
            queryBuilder.and(event.category.id.in(categories));
        }
        if (states != null && !states.isEmpty()) {
            queryBuilder.and(event.state.in(states));
        }

        if (queryBuilder.getValue() != null) {
            eventPage = eventRepository.findAll(queryBuilder, pageable);
        } else {
            eventPage = eventRepository.findAll(pageable);
        }
        List<Event> result = eventPage.getContent();
        setViews(result);
        return result.stream().map(eventMapper::toDto).toList();
    }

    @Override
    public EventFullDto updateEventFromAdmin(long eventId, UpdateEventAdminRequest adminDto) {
        log.info("Updating event from admin with Id {} ", eventId);
        Event event = findEventById(eventId);
        if (event.getState() != State.PENDING) {
            log.warn("Event with id {} is not state pending. Cannot perform this operation.", event.getId());
            throw new InvalidStateException("Event has already been published");
        }

        if (adminDto.getEventDate() != null
                && adminDto.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new ValidationException("The start time of the event to be modified must be no earlier than one hour" +
                    " from the date of publication");
        }

        if (adminDto.getStateAction() != null) {
            switch (adminDto.getStateAction()) {
                case PUBLISH_EVENT -> event.setState(State.PUBLISHED);
                case REJECT_EVENT -> event.setState(State.CANCELED);
                default -> throw new InvalidStateException("Invalid state action");
            }
        }

        Event savedEvent = updateEvent(event, adminDto.getCategoryId(), adminDto.getLocation(), adminDto.getAnnotation(),
                adminDto.getDescription(), adminDto.getEventDate(), adminDto.getPaid(), adminDto.getParticipantLimit(),
                adminDto.getRequestModeration(), adminDto.getTitle());
        setViews(List.of(savedEvent));
        log.info("Event {} updated from admin", savedEvent);
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getAllShortEventsForPublicUsers(String text, List<Long> categories,
                                                               Boolean paid, LocalDateTime rangeStart,
                                                               LocalDateTime rangeEnd, boolean onlyAvailable,
                                                               String sort, int from, int size) {
        Page<Event> eventPage;
        Pageable pageable;
        switch (sort) {
            case "EVENT_DATE" -> pageable = createPageable(from, size,
                    Sort.by(Sort.Direction.ASC, "eventDate"));
            case "VIEWS" -> pageable = createPageable(from, size,
                    Sort.by(Sort.Direction.ASC, "id"));
            case null -> pageable = createPageable(from, size, Sort.unsorted());
            default -> throw new ValidationException("Sort is not correct");
        }
//        if (sort.equals("EVENT_DATE")) {
//            pageable = createPageable(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));
//        } else {
//            pageable = Pageable.unpaged();
//        }
        BooleanBuilder queryBuilder = new BooleanBuilder();

        applyDateRangeFilter(rangeStart, rangeEnd, queryBuilder);

        if (text != null && !text.isBlank()) {
            queryBuilder.and(event.annotation.containsIgnoreCase(text.toLowerCase())
                    .or(event.description.containsIgnoreCase(text.toLowerCase())));
        }
        if (categories != null && !categories.isEmpty()) {
            queryBuilder.and(event.category.id.in(categories));
        }
        if (paid != null) {
            queryBuilder.and(event.paid.eq(paid));
        }
        if (onlyAvailable) {
            queryBuilder.and(event.participantLimit.eq(0)
                    .or(event.confirmedRequests.lt(event.participantLimit)));
        }
        if (queryBuilder.getValue() != null) {
            eventPage = eventRepository.findAll(queryBuilder, pageable);
        } else {
            eventPage = eventRepository.findAll(pageable);
        }
        List<Event> result = eventPage.getContent();
        setViews(result);
        if (sort != null && sort.equals("VIEWS")) {
            result = result.stream().sorted(Comparator.comparingLong(Event::getViews)).toList();
        }

        return result.stream().map(eventMapper::toShortDto).toList();
    }

    private void applyDateRangeFilter(LocalDateTime rangeStart, LocalDateTime rangeEnd, BooleanBuilder queryBuilder) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new ValidationException("Start time must be not after end time");
            }
            queryBuilder.and(event.eventDate.between(rangeStart, rangeEnd));
        } else if (rangeStart == null && rangeEnd != null) {
            queryBuilder.and(event.eventDate.before(rangeEnd));
        } else if (rangeStart != null) {
            queryBuilder.and(event.eventDate.after(rangeStart));
        }
    }

    @Override
    public EventFullDto getFullEventByIdForPublicUsers(long eventId) {
        Event event = findEventById(eventId);
        if (event.getState() != State.PUBLISHED) {
            throw new EntityNotFoundException("Event not found");
        }
        setViews(List.of(event));
        return eventMapper.toDto(event);
    }

    private Event updateEvent(Event event, Long categoryId, Location location, String annotation, String description,
                              LocalDateTime eventDate, Boolean paid, Integer participantLimit,
                              Boolean requestModeration, String title) {
        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            event.setCategory(category);
        }
        if (location != null) {
            Location newLocation = locationRepository.save(location);
            event.setLocation(newLocation);
        }

        Optional.ofNullable(annotation).ifPresent(event::setAnnotation);
        Optional.ofNullable(description).ifPresent(event::setDescription);
        Optional.ofNullable(eventDate).ifPresent(event::setEventDate);
        Optional.ofNullable(paid).ifPresent(event::setPaid);
        Optional.ofNullable(participantLimit).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(requestModeration).ifPresent(event::setRequestModeration);
        Optional.ofNullable(title).ifPresent(event::setTitle);

        return eventRepository.save(event);
    }

    private Event findEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id {} not found", eventId);
                    return new EntityNotFoundException("Event with id " + eventId + " not found");
                });
    }

    private Pageable createPageable(int from, int size, Sort sort) {
        log.debug("Create Pageable with offset from {}, size {}", from, size);
        return PageRequest.of(from / size, size, sort);
    }

    private User checkUserExist(long userId) {
        return userService.getUserById(userId);
    }

    private List<ViewStats> getViews(List<Event> events) {
        List<String> uris = events.stream().map(event -> "/events/" + event.getId()).toList();
        List<ViewStats> viewStats = statClient.getViewStats(LocalDateTime.now().minusYears(30),
                LocalDateTime.now(), uris, false);

        if (viewStats == null) {
            return Collections.emptyList();
        }
        return viewStats;
    }

    private void setViews(List<Event> events) {
        if (events.isEmpty()) {
            return;
        }
        Map<String, Long> mapUriAndHits = getViews(events).stream()
                .collect(Collectors.toMap(ViewStats::getUri, ViewStats::getHits));

        for (Event event : events) {
            event.setViews(mapUriAndHits.getOrDefault("/events/" + event.getId(), 0L));
        }
    }
}