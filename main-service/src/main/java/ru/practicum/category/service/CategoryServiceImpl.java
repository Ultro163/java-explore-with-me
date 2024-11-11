package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.error.exception.EntityNotFoundException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        log.info("Create category: {}", category);
        Category result = categoryRepository.save(category);
        log.info("Category created: {}", result);
        return result;
    }

    @Override
    public Category updateCategory(Category category) {
        log.info("Update category: {}", category);
        Category result = categoryRepository.save(category);
        log.info("Category updated: {}", result);
        return result;
    }

    @Override
    public void deleteCategory(long catId) {
        log.info("Delete category: {}", catId);
        categoryRepository.deleteById(catId);
        log.info("Category deleted: {}", catId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesWithPagination(int from, int size) {
        Pageable pageable = createPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return categoryRepository.findAll(pageable).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.warn("Category with id {} not found", catId);
                    return new EntityNotFoundException("Category with id " + catId + " not found");
                });
    }

    private Pageable createPageable(int from, int size, Sort sort) {
        log.debug("Create Pageable with offset from {}, size {}", from, size);
        return PageRequest.of(from / size, size, sort);
    }
}
