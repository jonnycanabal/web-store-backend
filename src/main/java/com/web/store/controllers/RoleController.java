package com.web.store.controllers;

import com.web.store.entity.Role;
import com.web.store.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService service;

    @GetMapping
    public ResponseEntity<?> viewAll() {

        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> viewById(@PathVariable Long id) {

        Optional<Role> currentRole = service.findById(id);

        if (!currentRole.isPresent()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found!");
        }

        return ResponseEntity.ok().body(currentRole.get());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Role role) {

        Role newRole = service.save(role);

        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Role role) {

        Optional<Role> currentRole = service.findById(id);

        if (!currentRole.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Role roleToModify = currentRole.get();

        if (role.getRoleName() != null) {
            roleToModify.setRoleName(role.getRoleName());
        }

        Role updatedRole = service.save(roleToModify);

        return ResponseEntity.status(HttpStatus.CREATED).body(updatedRole);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Optional<Role> currentRole = service.findById(id);

        if (!currentRole.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The role does not exist");
        }

        String roleName = currentRole.get().getRoleName();

        service.deleteById(id);

        return ResponseEntity.ok().body("The role '" + roleName + "' has been deleted");
    }
}
