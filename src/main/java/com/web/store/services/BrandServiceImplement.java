package com.web.store.services;

import com.web.store.entity.Brand;
import com.web.store.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BrandServiceImplement implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    @Transactional
    public Iterable<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<Brand> findById(Long id) {
        return brandRepository.findById(id);
    }

    @Override
    @Transactional
    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        brandRepository.deleteById(id);
    }

    @Override
    public List<Brand> findByBrandNameContainingIgnoreCase(String brandName) {
        return brandRepository.findByBrandNameContainingIgnoreCase(brandName);
    }
}
