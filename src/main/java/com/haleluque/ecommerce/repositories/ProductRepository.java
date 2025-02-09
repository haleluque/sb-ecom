package com.haleluque.ecommerce.repositories;

import com.haleluque.ecommerce.model.Category;
import com.haleluque.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryOrderByPriceAsc(Pageable pageDetails, Category category);
    Page<Product> findByProductNameContainingIgnoreCase(Pageable pageDetails, String keyword);
    boolean existsByProductName(String productName);
}
