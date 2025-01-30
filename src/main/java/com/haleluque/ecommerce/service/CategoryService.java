package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.dto.CategoryDTO;
import com.haleluque.ecommerce.dto.CategoryResponse;
import com.haleluque.ecommerce.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO category);
    CategoryDTO updateCategory(CategoryDTO category, Long categoryId);
    String deleteCategory(Long categoryId);
}
