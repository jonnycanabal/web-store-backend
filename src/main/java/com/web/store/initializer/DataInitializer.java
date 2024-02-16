package com.web.store.initializer;

import com.web.store.entity.Role;
import com.web.store.entity.ShoppingCart;
import com.web.store.entity.User;
import com.web.store.services.RoleService;
import com.web.store.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleService roleService;

    private final UserService userService;

    public DataInitializer(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!roleService.findByRoleName("ROLE_ADMIN").isPresent()) {
            Role adminRole = new Role("ROLE_ADMIN");
            roleService.save(adminRole);
        }

        if (!roleService.findByRoleName("ROLE_USER").isPresent()) {
            Role adminRole = new Role("ROLE_USER");
            roleService.save(adminRole);
        }

        Optional<User> existingAdminUser = userService.findByUsername("Admin");

        if (!existingAdminUser.isPresent()) {
            User userAdmin = new User();
            userAdmin.setFirstsName("User");
            userAdmin.setUsername("Admin");
            userAdmin.setPassword("12345");
            userAdmin.setEnabled(true);
            userAdmin.setAdmin(true);

            ShoppingCart cart = new ShoppingCart();
            cart.setUser(userAdmin);
            userAdmin.setShoppingCart(cart);

            userAdmin.setRoles(Collections.singletonList(roleService.findByRoleName("ROLE_ADMIN").get()));
            userService.save(userAdmin);
        }
    }
}
