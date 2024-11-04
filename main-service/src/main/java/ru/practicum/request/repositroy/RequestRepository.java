package ru.practicum.request.repositroy;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
}