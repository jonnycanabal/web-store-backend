package com.web.store.services;

import com.web.store.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Iterable<Product> findAll();

    Optional<Product> findById(Long id);

    Product save(Product product);

    void deleteById(Long id);

    List<Product> findAllByOrderById();

    List<Product> findByProductNameIgnoreCase(String productName);
}
