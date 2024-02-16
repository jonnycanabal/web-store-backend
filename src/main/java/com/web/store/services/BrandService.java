package com.web.store.services;

import com.web.store.entity.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    Iterable<Brand> findAll();

    Optional<Brand> findById(Long id);

    Brand save(Brand brand);

    void deleteById(Long id);

    public List<Brand> findByBrandNameContainingIgnoreCase(String brandName);
}
