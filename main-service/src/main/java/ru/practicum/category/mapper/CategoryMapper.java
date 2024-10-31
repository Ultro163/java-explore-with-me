package ru.practicum.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

@Mapper
public interface CategoryMapper {
    Category toEntity(CategoryDto categoryDto);

    CategoryDto toDto(Category category);
}