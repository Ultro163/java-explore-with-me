package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    User creat(User dto);

    List<UserDto> findUsersWithPagination(List<Integer> ids, int from, int size, String sort);

    void delete(long userId);

    User getUserById(long userId);
}