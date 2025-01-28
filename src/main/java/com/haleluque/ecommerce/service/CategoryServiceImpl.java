package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.model.Category;
import com.haleluque.ecommerce.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        Category selected = categoryOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "category with id " + categoryId + " not found in the list"));
        selected.setCategoryName(category.getCategoryName());
        return categoryRepository.save(selected);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        categoryRepository.delete(categoryOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found")));
        return "Category with categoryId: " + categoryId + " deleted successfully !!";
    }
}
