package com.web.store.controllers;

import com.web.store.entity.ShoppingCart;
import com.web.store.entity.User;
import com.web.store.repository.ShoppingCartRepository;
import com.web.store.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @GetMapping
    public ResponseEntity<?> viewAll() {

        return ResponseEntity.ok().body(userService.findAllByOrderById());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> viewById(@PathVariable Long id) {

        Optional<User> currentUser = userService.findById(id);

        if (!currentUser.isPresent()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }

        return ResponseEntity.ok().body(currentUser.get());
    }

    @PostMapping("/search")
    public ResponseEntity<?> findByName(@RequestBody Map<String, String> requestParams) {
        String firstsName = requestParams.get("firstsName");
        String middleName = requestParams.get("middleName");
        String lastName = requestParams.get("lastName");
        String secondLastName = requestParams.get("secondLastName");

        if (firstsName == null && middleName == null && lastName == null && secondLastName == null) {
            return ResponseEntity.badRequest().body("At least one parameter must be provided");
        }

        List<User> currentUser = userService.findByNamesIgnoreCase(
                firstsName != null ? firstsName : "",
                middleName != null ? middleName : "",
                lastName != null ? lastName : "",
                secondLastName != null ? secondLastName : ""
        );

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The User not found!");
        }

        return ResponseEntity.ok().body(currentUser);

    }

    @GetMapping("/search/url")
    public ResponseEntity<?> findByNameUrl(
            @RequestParam(required = false) String firstsName,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String secondLastName
    ) {

        if (firstsName == null && middleName == null && lastName == null && secondLastName == null) {
            return ResponseEntity.badRequest().body("At least one parameter must be provided");
        }

        List<User> currentUser = userService.findByNamesIgnoreCase(
                firstsName != null ? firstsName : "",
                middleName != null ? middleName : "",
                lastName != null ? lastName : "",
                secondLastName != null ? secondLastName : ""
        );

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The User not found!");
        }

        return ResponseEntity.ok().body(currentUser);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {

        if (result.hasFieldErrors()){
            return validation(result);
        }

        if (userService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("The username already exists");
        }

        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);

        user.setShoppingCart(cart);

        user.setEnabled(true);

        user.setAdmin(false);

        User newUser = userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(),"El campo" + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @PostMapping("/create/admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody User user) {

        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);

        user.setShoppingCart(cart);

        user.setEnabled(true);

        User newUser = userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody User user, @PathVariable Long id) {

        Optional<User> currentUser = userService.findById(id);

        if (!currentUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User userToModify = currentUser.get();

        if (user.getFirstsName() != null) {
            userToModify.setFirstsName(user.getFirstsName());
        }

        if (user.getMiddleName() != null) {
            userToModify.setMiddleName(user.getMiddleName());
        }

        if (user.getLastName() != null) {
            userToModify.setLastName(user.getLastName());
        }

        if (user.getSecondLastName() != null) {
            userToModify.setSecondLastName(user.getSecondLastName());
        }

        if (user.getPhoneNumber() != null) {
            userToModify.setPhoneNumber(user.getPhoneNumber());
        }

        if (user.getEmail() != null) {
            userToModify.setEmail(user.getEmail());
        }

        if (user.getUsername() != null) {
            userToModify.setUsername(user.getUsername());
        }

        if(user.getPassword() != null) {
            userToModify.setPassword(user.getPassword());
        }

        if (user.getEnabled() != null) {
            userToModify.setEnabled(user.getEnabled());
        }else{
            userToModify.setEnabled(userToModify.getEnabled());
        }

        if (user.isAdmin() != userToModify.isAdmin()) {
            userToModify.setAdmin(user.isAdmin());
        }

        User updatedUser = userService.save(userToModify);

        return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Optional<User> currentUser = userService.findById(id);

        if (!currentUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The user not found");
        }

        if (currentUser.get().getShoppingCart() != null) {

            Long idShoppingCart = currentUser.get().getShoppingCart().getId();

            try {
                shoppingCartRepository.deleteById(idShoppingCart);

            } catch (EmptyResultDataAccessException e) {

                System.out.println("ShoppingCart not found!");
            }
        }
        userService.deleteById(id);
        return ResponseEntity.ok().body(
                "The user " + currentUser.get().getFirstsName() + " has been successfully deleted");
    }

}
