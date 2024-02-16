package com.web.store.controllers;

import com.web.store.entity.CartItem;
import com.web.store.entity.ShoppingCart;
import com.web.store.repository.ShoppingCartRepository;
import com.web.store.services.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @GetMapping
    public ResponseEntity<?> viewAll() {
        return ResponseEntity.ok().body(shoppingCartService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> viewById(@PathVariable Long id) {

        Optional<ShoppingCart> currentShoppingCart = shoppingCartService.findById(id);

        if (!currentShoppingCart.isPresent()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shopping Cart not found!");
        }

        return ResponseEntity.ok().body(currentShoppingCart.get());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ShoppingCart shoppingCart) {

        ShoppingCart newShoppingCart = shoppingCartService.save(shoppingCart);

        return ResponseEntity.status(HttpStatus.CREATED).body(newShoppingCart);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody ShoppingCart shoppingCart, @PathVariable Long id) {

        Optional<ShoppingCart> currentShoppingCart = shoppingCartService.findById(id);

        if (!currentShoppingCart.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shopping Cart not found!");
        }

        ShoppingCart shoppingCartToModify = currentShoppingCart.get();

        if (shoppingCart.getProducts() != null) {
            shoppingCartToModify.setProducts(shoppingCart.getProducts());
        } else {
            shoppingCartToModify.setProducts(null);
        }

        if (shoppingCart.getUser() != null) {
            shoppingCartToModify.setUser(shoppingCart.getUser());
        }

        Set<CartItem> updatedItems = new HashSet<>();

        if (shoppingCart.getItems() != null) {

            for (CartItem item : shoppingCart.getItems()) {

                item.setShoppingCart(shoppingCartToModify);

                updatedItems.add(item);
            }
        } else {
            shoppingCartToModify.setItems(null);
        }

        shoppingCartToModify.getItems().clear();

        shoppingCartToModify.getItems().addAll(updatedItems);

        ShoppingCart updatedShoppingCart = shoppingCartService.save(shoppingCartToModify);

        return ResponseEntity.status(HttpStatus.CREATED).body(updatedShoppingCart);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Optional<ShoppingCart> currentShoppingCart = shoppingCartService.findById(id);

        if (!currentShoppingCart.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shopping Cart not found!");
        }

        ShoppingCart shoppingCartToModify = currentShoppingCart.get();

        shoppingCartToModify.getItems().clear();
        shoppingCartToModify.setProducts(null);
        if (shoppingCartToModify.getUser() != null) {
            shoppingCartToModify.getUser().setShoppingCart(null);
        }

        shoppingCartService.save(shoppingCartToModify);

        shoppingCartService.deleteById(id);

        return ResponseEntity.ok().body(
                "The Shopping Cart with ID '" + id + "' has been successfully deleted");

    }

    @GetMapping("/{id}/totalValue")
    public ResponseEntity<?> totalValue(@PathVariable long id) {

        Optional<ShoppingCart> currentShoppingCart = shoppingCartService.findById(id);

        if (!currentShoppingCart.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shopping Cart not found!");
        }

        ShoppingCart shoppingCart = currentShoppingCart.get();

        double total = shoppingCart.calculateTotal();

        return ResponseEntity.ok().body("The total shopping cart value is: $" + total);
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<ByteArrayResource> invoice(@PathVariable long id) throws IOException {
        try {
            byte[] invoicePDF = shoppingCartService.generateInvoice(id);

            if (invoicePDF.length == 0) {
                return ResponseEntity.notFound().build();
            }

            ByteArrayResource resource = new ByteArrayResource(invoicePDF);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Invoice.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource("Invoice not found!".getBytes()));
        }

    }

}
