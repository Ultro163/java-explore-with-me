package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.StatClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.error.exception.AccessDeniedException;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.error.exception.InvalidStateException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
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
        savedEvent.setViews(0);
        log.info("Event {} saved", savedEvent);
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventFullDto updateTheEventByTheUser(long userId, long eventId, UpdateEventUserRequest dto) {
        log.info("Updating event {}", dto);
        User user = checkUserExist(userId);
        Event event = findEventById(eventId);
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
                default -> throw new InvalidStateException("Invalid state action");
            }
        }
        if (user.getId().equals(event.getInitiator().getId())) {
            log.warn("User id {} is the dont same as initiator", userId);
            throw new AccessDeniedException("You do not have permission to update this event");
        }
        if (event.getState() == State.PUBLISHED) {
            log.warn("Event with id {} is already published. Cannot perform this operation.", event.getId());
            throw new InvalidStateException("Event has already been published");
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(dto.getCategoryId());
            event.setCategory(category);
        }
        if (dto.getLocation() != null) {
            Location location = locationRepository.save(dto.getLocation());
            event.setLocation(location);
        }

        Optional.ofNullable(dto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(dto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(dto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(dto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(dto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(dto.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(dto.getTitle()).ifPresent(event::setTitle);

        Event savedEvent = eventRepository.save(event);
        log.info("Event {} updated", savedEvent);
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        log.info("Getting events for userId {}, with offset from {}, size {} ", userId, from, size);
        checkUserExist(userId);
        Pageable pageable = createPageable(from / size, size, Sort.by(Sort.Direction.ASC, "createdOn"));
        List<Event> result = eventRepository.findAllByInitiatorId(userId, pageable);
        log.info("Found {} events", result.size());
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
        return eventMapper.toDto(event);
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
}