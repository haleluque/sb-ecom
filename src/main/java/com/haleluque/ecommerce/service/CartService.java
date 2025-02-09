package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.dto.CartDTO;
import org.springframework.security.core.Authentication;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity, Authentication authentication);
}
