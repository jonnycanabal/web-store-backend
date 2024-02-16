package com.web.store.controllers;

import com.web.store.entity.Product;
import com.web.store.services.ProductService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    @GetMapping
    public ResponseEntity<?> viewAll() {

        return ResponseEntity.ok().body(productService.findAllByOrderById());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> viewById(@PathVariable Long id) {

        Optional<Product> currentProduct = productService.findById(id);

        if (!currentProduct.isPresent()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }

        return ResponseEntity.ok().body(currentProduct.get());
    }

    @PostMapping("/search")
    public ResponseEntity<?> findByProductName(@RequestBody Map<String, String> requestParams) {
        String productName = requestParams.get("productName");

        if (productName == null) {
            return ResponseEntity.badRequest().body("At least one parameter must be provided");
        }

        List<Product> currentProduct = productService.findByProductNameIgnoreCase(
                productName
        );

        if (currentProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The Product not found!");
        }

        return ResponseEntity.ok().body(currentProduct);

    }

    @GetMapping("/search/url")
    public ResponseEntity<?> findByProductNameUrl(
            @RequestParam(required = false) String productName
    ) {

        if (productName == null) {
            return ResponseEntity.badRequest().body("At least one parameter must be provided");
        }

        List<Product> currentUser = productService.findByProductNameIgnoreCase(
                productName
        );

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The User not found!");
        }

        return ResponseEntity.ok().body(currentUser);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Product product) {

        Product newProduct = productService.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody Product product, @PathVariable Long id) {

        Optional<Product> currentProduct = productService.findById(id);

        if (!currentProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }

        Product productToModify = currentProduct.get();

        if (product.getProductName() != null) {
            productToModify.setProductName(product.getProductName());
        }

        if (product.getPrice() != null) {
            productToModify.setPrice(product.getPrice());
        }

        if (product.getBrand() != null){
            productToModify.setBrand(product.getBrand());
        }

        Product uptatedProduct = productService.save(productToModify);

        return ResponseEntity.status(HttpStatus.CREATED).body(uptatedProduct);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Optional<Product> currentProduct = productService.findById(id);

        if (!currentProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        productService.deleteById(id);

        return ResponseEntity.ok().body(
                "The Product " + currentProduct.get().getProductName() + " has been successfully deleted");
    }

//

    @GetMapping("/uploads/image/{id}")
    public ResponseEntity<?> viewByIdPhoto(@PathVariable Long id) {

        Optional<Product> currentProduct = productService.findById(id);

        if (!currentProduct.isPresent() || currentProduct.get().getPhoto() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "Product not found or product does not have image");
        }

        Resource image = new ByteArrayResource(currentProduct.get().getPhoto());

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

    @PostMapping("/create-with-photo")
    public ResponseEntity<?> createWithPhoto(@Valid Product product, BindingResult result,
                                             @RequestParam MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            product.setPhoto(file.getBytes());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }

    @PutMapping("/update-with-photo/{id}")
    public ResponseEntity<?> updateWithPhoto(@Valid Product product, BindingResult result, @PathVariable Long id,
                                             @RequestParam MultipartFile file) throws IOException {

        Optional<Product> currentProduct = productService.findById(id);

        if (!currentProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        Product productToModify = currentProduct.get();

        if (product.getProductName() != null) {
            productToModify.setProductName(product.getProductName());
        }

        if (product.getPrice() != null) {
            productToModify.setPrice(product.getPrice());
        }

        if (product.getBrand() != null){
            productToModify.setBrand(product.getBrand());
        }

        if (!file.isEmpty()) {
            productToModify.setPhoto(file.getBytes());
        }

        Product updatedProduct = productService.save(productToModify);

        return ResponseEntity.status(HttpStatus.CREATED).body(updatedProduct);
    }

}
