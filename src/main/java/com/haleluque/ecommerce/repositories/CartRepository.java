package com.haleluque.ecommerce.repositories;

import com.haleluque.ecommerce.model.Cart;
import com.haleluque.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE carts SET total_price=:totalPrice WHERE cart_id=:cartId")
    void updateCartItemTotalPrice(Long cartId, Double totalPrice);
}
