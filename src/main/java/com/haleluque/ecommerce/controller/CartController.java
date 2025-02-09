package com.haleluque.ecommerce.controller;

import com.haleluque.ecommerce.dto.CartDTO;
import com.haleluque.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("products/{productId}/quantity/{quantity}")
    public ResponseEntity<?> addProductToCart(@PathVariable Long productId,
                                              @PathVariable Integer quantity,
                                              Authentication authentication){
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity, authentication);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }
}
