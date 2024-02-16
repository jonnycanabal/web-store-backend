package com.web.store.services;

import com.web.store.entity.ShoppingCart;

import java.io.IOException;
import java.util.Optional;

public interface ShoppingCartService {

    Iterable<ShoppingCart> findAll();

    Optional<ShoppingCart> findById(Long id);

    ShoppingCart save(ShoppingCart shoppingCart);

    void deleteById(Long id);

    byte[] generateInvoice(Long id) throws IOException;

    String invoiceContent(Long id) throws IOException;
}
