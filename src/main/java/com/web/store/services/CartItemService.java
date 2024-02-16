package com.web.store.services;

import com.web.store.entity.CartItem;

import java.util.Optional;

public interface CartItemService {

    Iterable<CartItem> findAll();

    Optional<CartItem> findById(Long id);

    CartItem save(CartItem cartItem);

    void deleteById(Long id);
}
