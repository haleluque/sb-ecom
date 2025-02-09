package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.dto.CartDTO;
import com.haleluque.ecommerce.dto.CartItemDTO;
import com.haleluque.ecommerce.dto.ProductDTO;
import com.haleluque.ecommerce.exceptions.ApiException;
import com.haleluque.ecommerce.exceptions.ResourceNotFoundException;
import com.haleluque.ecommerce.model.Cart;
import com.haleluque.ecommerce.model.CartItem;
import com.haleluque.ecommerce.model.Product;
import com.haleluque.ecommerce.model.User;
import com.haleluque.ecommerce.repositories.CartItemRepository;
import com.haleluque.ecommerce.repositories.CartRepository;
import com.haleluque.ecommerce.repositories.ProductRepository;
import com.haleluque.ecommerce.repositories.UserRepository;
import com.haleluque.ecommerce.security.services.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    //@Transactional
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity, Authentication authentication) {
        if (quantity == 0) {
            throw new ApiException(quantity + " is not a valid quantity");
        }

        //Extract the user info from the principal
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(User.class.getSimpleName(), "userId", userId));

        //Find the product
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Product.class.getSimpleName(), "productId", productId));

        //Validate if the cart exists, otherwise it must be created
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));
        List<CartItem> cartItems;

        //add the new cart item to the cart, otherwise a new cart Item is added
        boolean cartItemExists = cart.getCartItems().stream().anyMatch(cartItem -> cartItem.getProduct().getProductId().equals(productId));
        cartItems = cartItemExists ?
                updateExistingCartItem(cart, quantity) :
                addNewCartItemToCart(cart, product, quantity);


        //Updates the cart total price
        calculateCartTotalPrice(quantity, cart, cartItems);
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setCartItems(
                cartItems.stream()
                        .map(item ->
                                {
                                    //Cast the product dto individually
                                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                                    item.setProduct(null);
                                    CartItemDTO cartItemDTO = modelMapper.map(item, CartItemDTO.class);
                                    cartItemDTO.setProduct(productDTO);
                                    return cartItemDTO;
                                }
                        ).toList());
        return cartDTO;
    }

    private void calculateCartTotalPrice(Integer quantity, Cart cart, List<CartItem> cartItems) {
        cart.setTotalPrice(cartItems
                .stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity()).sum());
        cartRepository.save(cart);
    }

    private List<CartItem> updateExistingCartItem(Cart cart, Integer quantity) {
        List<CartItem> cartItems = cart.getCartItems();
        cartItems
                .stream()
                .findFirst()
                .ifPresent(cartItem -> {
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);
                    cartItemRepository.save(cartItem);
                });
        return cartItems;
    }

    private List<CartItem> addNewCartItemToCart(Cart cart, Product product, Integer quantity) {
        CartItem cartItem = new CartItem(product.getDiscount(), product.getSpecialPrice(), quantity, product);
        cartItem.setCart(cart);
        cartItemRepository.save(cartItem);
        return List.of(cartItem);
    }

    private Cart createNewCart(User user) {
        Cart newCart = new Cart(0.0, user);
        return cartRepository.save(newCart);
    }
}
