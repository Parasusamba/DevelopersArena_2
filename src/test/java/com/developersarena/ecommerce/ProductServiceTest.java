package com.developersarena.ecommerce;

import com.developersarena.ecommerce.dto.ProductRequest;
import com.developersarena.ecommerce.dto.ProductResponse;
import com.developersarena.ecommerce.entity.Category;
import com.developersarena.ecommerce.entity.Product;
import com.developersarena.ecommerce.repository.CartRepository;
import com.developersarena.ecommerce.repository.CategoryRepository;
import com.developersarena.ecommerce.repository.ProductRepository;
import com.developersarena.ecommerce.service.CartService;
import com.developersarena.ecommerce.service.ProductService;
import com.developersarena.ecommerce.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private ProductServiceImpl productService;
    private ProductRequest productRequest;
    private Category category;
    private Product product;
    @BeforeEach
    public void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();
        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(BigDecimal.valueOf(100.0));
        productRequest.setQuantity(10);
        productRequest.setCategoryId(1L);

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(100.0))
                .quantity(10)
                .category(category)
                .build();
    }
    @Test
    void createProduct_Success() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponse response = productService.createProduct(productRequest);

        assertNotNull(response);
        assertEquals("Test Product", response.getName());
        assertEquals(BigDecimal.valueOf(100.0), response.getPrice());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ProductResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Product", response.getName());
    }
}
