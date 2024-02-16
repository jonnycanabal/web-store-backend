package com.web.store.services;

import com.web.store.entity.Role;

import java.util.Optional;

public interface RoleService {

    Iterable<Role> findAll();

    Optional<Role> findById(Long id);

    Role save(Role role);

    void deleteById(Long id);

    Optional<Role> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);
}
