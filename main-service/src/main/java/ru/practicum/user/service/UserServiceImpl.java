package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User creat(User user) {
        log.info("Creating user {}", user);
        User resultUser = userRepository.save(user);
        log.info("Created user {}", resultUser);
        return resultUser;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersWithParams(List<Integer> ids, int from, int size) {
        log.info("Getting users with params");
        if (ids == null || ids.isEmpty()) {
            log.info("Get users with offset from {}, size {}", from, size);
            Pageable pageable = createPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
            return userRepository.findAllUsersWithPagination(pageable);
        } else {
            log.info("Get users with ids {}", ids);
            return userRepository.findByIds(ids);
        }
    }

    @Override
    public void delete(long userId) {
        log.info("Deleting user with ID = {}", userId);
        userRepository.deleteById(userId);
        log.info("Deleted user with ID = {}", userId);
    }

    private Pageable createPageable(int from, int size, Sort sort) {
        log.debug("Create Pageable with offset from {}, size {}", from, size);
        return PageRequest.of(from / size, size, sort);
    }
}