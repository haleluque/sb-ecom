package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.dto.CategoryDTO;
import com.haleluque.ecommerce.dto.CategoryResponse;
import com.haleluque.ecommerce.exceptions.ApiException;
import com.haleluque.ecommerce.exceptions.ResourceNotFoundException;
import com.haleluque.ecommerce.model.Category;
import com.haleluque.ecommerce.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        //using modelMapper
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryPage
                .stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList());
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (this.checkIfCategoryNameExist(categoryDTO.getCategoryName()))
            throw new ApiException("Category with the name " + categoryDTO.getCategoryName() + " already exist");
        Category saved = categoryRepository.save(modelMapper.map(categoryDTO, Category.class));
        return modelMapper.map(saved, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        Category selected = categoryOptional.orElseThrow(() -> new ResourceNotFoundException(Category.class.getSimpleName(), "id", categoryId));
        selected.setCategoryName(categoryDTO.getCategoryName());
        Category saved = categoryRepository.save(selected);
        return modelMapper.map(saved, CategoryDTO.class);
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
