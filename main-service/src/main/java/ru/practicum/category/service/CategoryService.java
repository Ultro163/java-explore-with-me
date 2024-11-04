package ru.practicum.category.service;

import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);

    Category updateCategory(Category category);

    void deleteCategory(long catId);

    List<Category> getCategoriesWithPagination(int from, int size);

    Category getCategoryById(long catId);
}