package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.dto.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.error.exception.EntityNotFoundException;
import ru.practicum.event.repository.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        log.info("Creating new compilation: {}", dto);
        Compilation compilation = mapper.toEntity(dto);
        Set<Long> eventsId = dto.getEvents();

        if (!eventsId.isEmpty()) {
            compilation.setEvents(eventRepository.findAllByIdIn(eventsId));
        }
        Compilation saved = compilationRepository.save(compilation);
        log.info("Saved compilation: {}", saved);
        return mapper.toDto(saved);
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest dto) {
        log.info("Updating compilation: {}", dto);
        Compilation compilation = getCompilation(compId);
        Set<Long> eventsId = dto.getEvents();
        if (!eventsId.isEmpty()) {
            compilation.setEvents(eventRepository.findAllByIdIn(eventsId));
        }
        Optional.ofNullable(dto.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(dto.getPinned()).ifPresent(compilation::setPinned);
        Compilation saved = compilationRepository.save(compilation);
        log.info("Updated compilation: {}", saved);
        return mapper.toDto(saved);
    }

    @Override
    public void deleteCompilation(long compId) {
        log.info("Deleting compilation: {}", compId);
        getCompilation(compId);
        compilationRepository.deleteById(compId);
        log.info("Deleted compilation: {}", compId);

    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("Getting compilations with offset: pinned={}, from={}, size={}", pinned, from, size);
        Pageable pageable = PageRequest.of(from / size, size, Sort.unsorted());
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, pageable).stream().map(mapper::toDto).toList();
        } else {
            return compilationRepository.findAll(pageable).getContent().stream().map(mapper::toDto).toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(int compId) {
        log.info("Getting compilation with Id: {}", compId);
        return mapper.toDto(getCompilation(compId));
    }


    private Compilation getCompilation(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Compilation with id " + compId + " not found"));
    }
}