package com.web.store.controllers;

import com.web.store.entity.Brand;
import com.web.store.services.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    public ResponseEntity<?> viewAll() {

        return ResponseEntity.ok().body(brandService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> view(@PathVariable Long id) {

        Optional<Brand> currentBrand = brandService.findById(id);

        if (!currentBrand.isPresent()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Brand not found!");
        }

        return ResponseEntity.ok().body(currentBrand.get());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Brand brand) {

        Brand newBrand = brandService.save(brand);

        return ResponseEntity.status(HttpStatus.CREATED).body(newBrand);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody Brand brand, @PathVariable Long id) {

        Optional<Brand> currentBrand = brandService.findById(id);

        if (!currentBrand.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Brand brandToModify = currentBrand.get();

        if (brand.getBrandName() != null) {
            brandToModify.setBrandName(brand.getBrandName());
        }

        Brand updatedBrand = brandService.save(brandToModify);

        return ResponseEntity.status(HttpStatus.CREATED).body(updatedBrand);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Optional<Brand> currentBrand = brandService.findById(id);

        if (!currentBrand.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The brand not found!");
        }

        brandService.deleteById(id);

        return ResponseEntity.ok().body(
                "The brand " + currentBrand.get().getBrandName() + " has been successfully deleted");
    }

//

    @GetMapping("/uploads/image/{id}")
    public ResponseEntity<?> viewByIdPhoto(@PathVariable Long id) {

        Optional<Brand> currentBrand = brandService.findById(id);

        if (!currentBrand.isPresent() || currentBrand.get().getPhoto() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "Brand not found or brand does not have image");
        }

        Resource image = new ByteArrayResource(currentBrand.get().getPhoto());

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @PostMapping("/create-with-photo")
    public ResponseEntity<?> createWithPhoto(@Valid Brand brand, BindingResult result,
                                             @RequestParam MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            brand.setPhoto(file.getBytes());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.save(brand));
    }

    @PutMapping("/update-with-photo/{id}")
    public ResponseEntity<?> editWithPhoto(@Valid Brand brand, BindingResult result, @PathVariable Long id,
                                           @RequestParam MultipartFile file) throws IOException {

        Optional<Brand> currentBrand = brandService.findById(id);

        if (!currentBrand.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Brand updatedBrand = currentBrand.get();

//        updatedBrand.setBrandName(brand.getBrandName());

        if (brand.getBrandName() != null){
            updatedBrand.setBrandName(brand.getBrandName());
        }

        if (!file.isEmpty()) {
            updatedBrand.setPhoto(file.getBytes());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.save(updatedBrand));
    }

}
