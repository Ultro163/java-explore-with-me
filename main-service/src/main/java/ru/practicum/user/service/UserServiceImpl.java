package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.error.exception.ValidationException;
import ru.practicum.event.util.CalculateRating;
import ru.practicum.like.dto.RatingDto;
import ru.practicum.like.model.EventLike;
import ru.practicum.like.repository.LikeRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final UserMapper mapper;

    @Override
    public User creat(User user) {
        log.info("Creating user {}", user);
        User resultUser = userRepository.save(user);
        log.info("Created user {}", resultUser);
        return resultUser;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findUsersWithPagination(List<Integer> ids, int from, int size, String sort) {
        log.info("Getting users with params");
        log.debug("Create Pageable with offset from {}, size {}, sort={}", from, size, sort);
        Pageable pageable = Pageable.unpaged();
        boolean sortRating = false;
        switch (sort) {
            case "RATING" -> sortRating = true;
            case null -> pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
            default -> throw new ValidationException("Sort is not supported: " + sort.toLowerCase());
        }

        if (ids == null || ids.isEmpty()) {
            log.info("Get users with offset from {}, size {}", from, size);
            List<User> users = userRepository.findAll(pageable).getContent();
            settingUsersRating(users);
            if (sortRating) {
                users = users.stream().sorted(Comparator.comparingDouble(User::getRating).reversed()).toList();
            }
            return users.stream().map(mapper::toDto).toList();
        } else {
            log.info("Get users with ids {}", ids);
            List<User> users = userRepository.findByIds(ids, pageable);
            settingUsersRating(users);
            if (sortRating) {
                users = users.stream().sorted(Comparator.comparingDouble(User::getRating).reversed()).toList();
            }
            return users.stream().map(mapper::toDto).toList();
        }
    }

    private void settingUsersRating(List<User> users) {
        List<Long> userIds = users.stream().map(User::getId).toList();
        Map<Long, List<EventLike>> mapUserLikes = likeRepository.findLikes(userIds).stream()
                .collect(Collectors.groupingBy(
                        RatingDto::getUserId,
                        Collectors.mapping(
                                RatingDto::getEventLike,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream().filter(Objects::nonNull).collect(Collectors.toList())
                                )
                        )
                ));
        for (User user : users) {
            List<EventLike> eventLikes = mapUserLikes.getOrDefault(user.getId(), Collections.emptyList());
            double rating = CalculateRating.calculateRating(eventLikes);
            user.setRating(rating);
        }
    }

    @Override
    public void delete(long userId) {
        log.info("Deleting user with ID = {}", userId);
        userRepository.deleteById(userId);
        log.info("Deleted user with ID = {}", userId);
    }

    @Override
    public User getUserById(long userId) {
        log.info("Getting user with ID = {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", userId);
                    return new EntityNotFoundException("User with ID " + userId + " not found");
                });
    }
}