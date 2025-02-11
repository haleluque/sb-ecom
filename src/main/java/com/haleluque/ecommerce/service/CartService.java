package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.dto.CartDTO;

import java.util.List;

public interface CartService {
    List<CartDTO> getAllCarts();
    CartDTO getCartByLoggedUser();
    CartDTO addProductToCart(Long productId, Integer quantity);
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);
    String deleteProductFromCart(Long cartId, Long productId);
}
