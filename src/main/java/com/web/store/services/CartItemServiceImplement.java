package com.web.store.services;

import com.web.store.entity.CartItem;
import com.web.store.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CartItemServiceImplement implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public Iterable<CartItem> findAll() {
        return cartItemRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<CartItem> findById(Long id) {
        return cartItemRepository.findById(id);
    }

    @Override
    @Transactional
    public CartItem save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }
}
