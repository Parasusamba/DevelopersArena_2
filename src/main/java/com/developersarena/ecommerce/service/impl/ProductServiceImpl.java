package com.developersarena.ecommerce.service.impl;

import com.developersarena.ecommerce.dto.CategoryResponse;
import com.developersarena.ecommerce.dto.ProductRequest;
import com.developersarena.ecommerce.dto.ProductResponse;
import com.developersarena.ecommerce.entity.Product;
import com.developersarena.ecommerce.exception.NotFoundException;
import com.developersarena.ecommerce.repository.CategoryRepository;
import com.developersarena.ecommerce.repository.ProductRepository;
import com.developersarena.ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountedPrice(request.getDiscountedPrice());
        product.setQuantity(request.getQuantity());
        product.setBrand(request.getBrand());
        product.setColor(request.getColor());
        product.setSize(request.getSize());
        product.setImageUrl(request.getImageUrl());
        Optional.ofNullable(request.getCategoryId())
                .map(categoryRepository::findById)
                .map(opt -> opt.orElseThrow(() ->
                        new NotFoundException("Category not found with id: " + request.getCategoryId(),
                                "CATEGORY_NOT_FOUND"
                        )))
                .ifPresent(product::setCategory);

        product = productRepository.save(product);
        return convertToResponse(product);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::convertToResponse);
    }
    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id, "PRODUCT_NOT_FOUND"));
        return convertToResponse(product);
    }
    @Transactional
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request)  {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id, "PRODUCT_NOT_FOUND"));

        Optional.ofNullable(request.getName()).ifPresent(product::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(request.getPrice()).ifPresent(product::setPrice);
        Optional.ofNullable(request.getQuantity()).ifPresent(product::setQuantity);
        Optional.ofNullable(request.getSize()).ifPresent(product::setSize);
        Optional.ofNullable(request.getColor()).ifPresent(product::setColor);
        Optional.ofNullable(request.getBrand()).ifPresent(product::setBrand);
        Optional.ofNullable(request.getDiscountedPrice()).ifPresent(product::setDiscountedPrice);
        Optional.ofNullable(request.getImageUrl()).ifPresent(product::setImageUrl);
        Optional.ofNullable(request.getCategoryId())
                .map(categoryRepository::findById)
                .map(opt -> opt.orElseThrow(() ->
                        new NotFoundException("Category not found with id: " + id,
                                "CATEGORY_NOT_FOUND"
                        )))
                .ifPresent(product::setCategory);


        productRepository.save(product);
        return convertToResponse(product);

    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found with id: " + id, "PRODUCT_NOT_FOUND");
        }
        productRepository.deleteById(id);
    }
    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable)
                .map(this::convertToResponse);
    }
    @Override
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(this::convertToResponse);
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setDiscountedPrice(product.getDiscountedPrice());
        response.setQuantity(product.getQuantity());
        response.setBrand(product.getBrand());
        response.setColor(product.getColor());
        response.setSize(product.getSize());
        response.setImageUrl(product.getImageUrl());
        response.setRating(product.getRating());
        response.setReviewCount(product.getReviewCount());
        response.setCreatedAt(product.getCreatedAt());

        if (product.getCategory() != null) {
            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setId(product.getCategory().getId());
            categoryResponse.setName(product.getCategory().getName());
            categoryResponse.setDescription(product.getCategory().getDescription());
            categoryResponse.setImageUrl(product.getCategory().getImageUrl());
            categoryResponse.setCreatedAt(product.getCategory().getCreatedAt());
            response.setCategory(categoryResponse);
        }
        return response;
    }
}
