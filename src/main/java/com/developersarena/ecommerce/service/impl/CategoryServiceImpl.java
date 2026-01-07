package com.developersarena.ecommerce.service.impl;

import com.developersarena.ecommerce.dto.CategoryResponse;
import com.developersarena.ecommerce.entity.Category;
import com.developersarena.ecommerce.exception.NotFoundException;
import com.developersarena.ecommerce.repository.CategoryRepository;
import com.developersarena.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::convertToResponse).orElseThrow(() ->
                        new NotFoundException("Category not found with id: " + id,
                                "CATEGORY_NOT_FOUND"));
    }
    @Override
    public CategoryResponse createCategory(Category category) {
        if(categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category already exits");
        }
        category = categoryRepository.save(category);
        return convertToResponse(category);
    }
    @Override
    public CategoryResponse updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                        new NotFoundException("Category not found with id: " + id,
                                "CATEGORY_NOT_FOUND"));
        String name = categoryDetails.getName() != null ? categoryDetails.getName() : category.getName();
        if(!category.getName().equals(name) &&
            categoryRepository.existsByName(categoryDetails.getName())) {
            throw new RuntimeException("Category already exits");
        }
        Optional.ofNullable(categoryDetails.getName()).ifPresent(category::setName);
        Optional.ofNullable(categoryDetails.getDescription()).ifPresent(category::setDescription);
        Optional.ofNullable(categoryDetails.getImageUrl()).ifPresent(category::setImageUrl);

        category = categoryRepository.save(category);
        return convertToResponse(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if(!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category not found with id: " + id,
                    "CATEGORY_NOT_FOUND");
        }
        categoryRepository.deleteById(id);
    }


    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setImageUrl(category.getImageUrl());
        response.setCreatedAt(category.getCreatedAt());
        return response;
    }
}
