package com.web.store.repository;

import com.web.store.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    List<Brand> findByBrandNameContainingIgnoreCase(String brandName);
}
