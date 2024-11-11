package ru.practicum.user.service;

import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    User creat(User dto);

    List<User> findUsersWithPagination(List<Integer> ids, int from, int size);

    void delete(long userId);

    User getUserById(long userId);
}