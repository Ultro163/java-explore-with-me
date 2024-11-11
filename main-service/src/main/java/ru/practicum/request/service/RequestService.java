package ru.practicum.request.service;

import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestService {

    Request addRequest(long userId, long eventId);

    List<Request> getRequests(long userId);

    Request updateRequest(long userId, long requestId);
}
