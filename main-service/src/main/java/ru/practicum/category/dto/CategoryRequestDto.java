package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link ru.practicum.category.model.Category}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDto {
    @NotNull
    @Size(max = 50)
    @NotBlank
    private String name;
}