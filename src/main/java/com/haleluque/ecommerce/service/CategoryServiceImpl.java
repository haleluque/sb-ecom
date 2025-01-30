package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.exceptions.ApiException;
import com.haleluque.ecommerce.exceptions.ResourceNotFoundException;
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
    public List<Category> getAllCategories() { return categoryRepository.findAll(); }

    @Override
    public void createCategory(Category category) {
        if (this.checkIfCategoryNameExist(category.getCategoryName()))
            throw new ApiException("Category with the name "+ category.getCategoryName() + " already exist");
        categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        Category selected = categoryOptional.orElseThrow(() -> new ResourceNotFoundException(Category.class.getSimpleName(), "id", categoryId));
        selected.setCategoryName(category.getCategoryName());
        return categoryRepository.save(selected);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        categoryRepository.delete(categoryOptional.orElseThrow(() -> new ResourceNotFoundException(Category.class.getSimpleName(), "id", categoryId)));
        return "Category with categoryId: " + categoryId + " deleted successfully !!";
    }

    private boolean checkIfCategoryNameExist(String name) {
        Category category = categoryRepository.findByCategoryName(name);
        return category != null;
    }
}
