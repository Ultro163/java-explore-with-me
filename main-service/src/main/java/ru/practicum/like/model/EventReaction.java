package ru.practicum.like.model;

import ru.practicum.error.exception.ValidationException;

public enum EventReaction {
    LIKE, DISLIKE;

    public static EventReaction fromString(String reaction) {
        try {
            return EventReaction.valueOf(reaction.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Unknown state: %s", reaction));
        }
    }
}