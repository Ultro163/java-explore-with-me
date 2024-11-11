package ru.practicum.compilation.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.mapper.EventMapper;

@Mapper(uses = {EventMapper.class})
public interface CompilationMapper {
    Compilation toEntity(CompilationDto compilationDto);

    CompilationDto toDto(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    Compilation toEntity(NewCompilationDto compilationDto);

    @Mapping(target = "events", ignore = true)
    Compilation toEntity(UpdateCompilationRequest updateCompilationRequest);
}