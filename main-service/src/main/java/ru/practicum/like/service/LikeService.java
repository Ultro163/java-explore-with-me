package ru.practicum.like.service;

public interface LikeService {

    void evaluationForEventByUser(long userId, long eventId, String reaction);

    void updateEvaluationForEventByUser(long userId, long eventId, String reaction);

    void deleteEvaluationForEventByUser(long userId, long eventId);
}