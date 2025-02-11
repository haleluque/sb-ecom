package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.dto.CartDTO;
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
import com.haleluque.ecommerce.utils.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Autowired
    private AuthUtil authUtil;

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return carts.stream().map(cart -> transformCartToCartDto(cart, cart.getCartItems())).toList();
    }

    @Override
    public CartDTO getCartByLoggedUser() {
        //Extract the user info from the principal
        User user = authUtil.loggedInUser();
        Optional<Cart> cart = cartRepository.findByUser(user);
        return cart.isPresent() ? modelMapper.map(cart.get(), CartDTO.class) : new CartDTO();
    }

    @Transactional
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        if (quantity == 0) {
            throw new ApiException(quantity + " is not a valid quantity");
        }

        //Extract the user info from the principal
        User user = authUtil.loggedInUser();

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
                sumExistingCartItemQuantity(cart, quantity) :
                addNewCartItemToCart(cart, product, quantity);

        //Updates the cart total price
        calculateCartTotalPrice(cart, cartItems);
        return transformCartToCartDto(cart, cartItems);
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        //Find the product
        productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Product.class.getSimpleName(), "productId", productId));

        //Extract the user info from the principal
        User user = authUtil.loggedInUser();
        Optional<Cart> optionalCart = cartRepository.findByUser(user);

        //Validates if the cart exists
        if (optionalCart.isEmpty())
            return new CartDTO();

        Cart cart = optionalCart.get();
        List<CartItem> cartItems;

        //Check if the user has a cart with that productId
        boolean cartItemExists = cart.getCartItems().stream().anyMatch(cartItem -> cartItem.getProduct().getProductId().equals(productId));
        if (!cartItemExists || quantity < 0) {
            return new CartDTO();
        } //If quantity is zero, that means the cart item must be removed
        else if (quantity == 0) {
            cartItems = removeProductFromCart(productId, cart);
        } else {
            cartItems = updateExistingCartQuantityValue(cart, quantity, productId);
            calculateCartTotalPrice(cart, cartItems);
        }
        return transformCartToCartDto(cart, cartItems);
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Optional<Cart> cart = cartRepository.findById(cartId);
        cart.ifPresent(value -> {
            removeProductFromCart(productId, value);
            cartItemRepository.flush();
        });
        return "The product with id: " + productId + " was removed successfully";
    }

    private List<CartItem> removeProductFromCart(Long productId, Cart cart) {
        Optional<CartItem> removeCart = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        if (removeCart.isPresent()) {
            cartItemRepository.deleteByCartId(removeCart.get().getCartItemId());

            cart.getCartItems().removeIf(item -> item.getCartItemId().equals(removeCart.get().getCartItemId()));

            // Recalculate total price after item removal
            calculateCartTotalPrice(cart, cart.getCartItems());

            return cart.getCartItems();
        }
        return cart.getCartItems();
    }

    private CartDTO transformCartToCartDto(Cart cart, List<CartItem> cartItems) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setProductDTOS(
                cartItems.stream().map(item -> {
                    item.getProduct().setQuantity(item.getQuantity());
                    return modelMapper.map(item.getProduct(), ProductDTO.class);
                }).toList()
        );
        return cartDTO;
    }

    private void calculateCartTotalPrice(Cart cart, List<CartItem> cartItems) {
        Double totalPrice = cartItems
                .stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity()).sum();
        cartRepository.updateCartItemTotalPrice(cart.getCartId(), totalPrice);
    }

    private List<CartItem> updateExistingCartQuantityValue(Cart cart, Integer quantity, Long productId) {
        List<CartItem> cartItems = cart.getCartItems();
        cartItems.stream()
                .filter(cartItem -> cartItem.getProduct().getProductId().equals(productId))
                .forEach(cartItem -> {
                    cartItem.setQuantity(quantity);
                    cartItemRepository.save(cartItem);
                });
        return cartItems;
    }

    private List<CartItem> sumExistingCartItemQuantity(Cart cart, Integer quantity) {
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
