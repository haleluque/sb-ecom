package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.dto.ProductDTO;
import com.haleluque.ecommerce.dto.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductByCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, Long categoryId);

    ProductResponse getProductByKeyword(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword);

    ProductDTO addProduct(Long categoryId, ProductDTO product);

    ProductDTO updateProduct(Long productId, ProductDTO product);

    String deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
