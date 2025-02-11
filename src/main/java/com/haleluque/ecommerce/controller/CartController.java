package com.haleluque.ecommerce.controller;

import com.haleluque.ecommerce.dto.CartDTO;
import com.haleluque.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts() {
        List<CartDTO> cartDTOs = cartService.getAllCarts();
        return new ResponseEntity<List<CartDTO>>(cartDTOs, HttpStatus.FOUND);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<?> getCartById() {
        CartDTO cartDTO = cartService.getCartByLoggedUser();
        return new ResponseEntity<>(cartDTO.getCartId() != null ? cartDTO : "The user does not have any cart", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("products/{productId}/quantity/{quantity}")
    public ResponseEntity<?> addProductToCart(@PathVariable Long productId,
                                              @PathVariable Integer quantity) {
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/products/{productId}/quantity/{quantity}")
    public ResponseEntity<?> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable Integer quantity) {

        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId, quantity);
        if (cartDTO.getCartId() == null)
            return new ResponseEntity<>("Cart's product was updated successfully", HttpStatus.OK);

        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId) {
        String status = cartService.deleteProductFromCart(cartId, productId);

        return new ResponseEntity<String>(status, HttpStatus.OK);
    }
}
