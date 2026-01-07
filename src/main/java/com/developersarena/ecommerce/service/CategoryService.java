package com.developersarena.ecommerce.service;

import com.developersarena.ecommerce.dto.CategoryResponse;
import com.developersarena.ecommerce.entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(Category category);

    CategoryResponse updateCategory(Long id, Category categoryDetails);

    void deleteCategory(Long id);
}
