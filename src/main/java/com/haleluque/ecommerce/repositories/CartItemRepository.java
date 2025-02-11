package com.haleluque.ecommerce.repositories;

import com.haleluque.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM cart_items ci WHERE ci.cart_id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);

    @Query(nativeQuery = true, value = "SELECT COUNT(c) FROM cart_items c WHERE c.product_id = :productId")
    int countByProductId(@Param("productId") Long productId);
}
