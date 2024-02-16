package com.web.store.controllers;

import com.web.store.entity.CartItem;
import com.web.store.services.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/cartItem")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @GetMapping
    public ResponseEntity<?> viewAll() {
        return ResponseEntity.ok().body(cartItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> viewById(@PathVariable Long id) {

        Optional<CartItem> currentCartItem = cartItemService.findById(id);

        if (!currentCartItem.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart Item not found!");
        }

        return ResponseEntity.ok().body(currentCartItem.get());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CartItem cartItem) {

        CartItem newCartItem = cartItemService.save(cartItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(newCartItem);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody CartItem cartItem, @PathVariable Long id) {

        Optional<CartItem> currentCartItem = cartItemService.findById(id);

        if (!currentCartItem.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        CartItem CartItemToModify = currentCartItem.get();

        if (cartItem.getShoppingCart() != null) {
            CartItemToModify.setShoppingCart(cartItem.getShoppingCart());
        }

        if (cartItem.getProduct() != null) {
            CartItemToModify.setProduct(cartItem.getProduct());
        }

        if (cartItem.getQuantity() != null) {
            CartItemToModify.setQuantity(cartItem.getQuantity());
        }

        CartItem updatedCartItem = cartItemService.save(CartItemToModify);

        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCartItem);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Optional<CartItem> currentCartItem = cartItemService.findById(id);

        if (!currentCartItem.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CartItem not found!");
        }

        cartItemService.deleteById(id);

        return ResponseEntity.ok().body("The cartItem with ID: '" + id + "' has been successfully deleted");
    }

}
