package ru.practicum.like.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.like.service.LikeService;

@RestController
@RequestMapping("/users/{userId}/reactions")
@RequiredArgsConstructor
public class LikePrivateController {
    private final LikeService likeServiceImpl;

    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void evaluationForEventByUser(@PathVariable long userId, @PathVariable long eventId,
                                         @RequestParam String reaction) {
        likeServiceImpl.evaluationForEventByUser(userId, eventId, reaction);
    }

    @PatchMapping("/events/{eventId}")
    public void updateEvaluationForEventByUser(@PathVariable long userId, @PathVariable long eventId,
                                               @RequestParam String reaction) {
        likeServiceImpl.updateEvaluationForEventByUser(userId, eventId, reaction);
    }

    @DeleteMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvaluationForEventByUser(@PathVariable long userId, @PathVariable long eventId) {
        likeServiceImpl.deleteEvaluationForEventByUser(userId, eventId);
    }
}