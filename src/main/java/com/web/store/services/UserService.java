package com.web.store.services;

import com.web.store.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Iterable<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    void deleteById(Long id);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    List<User> findAllByOrderById();

    List<User> findByFirstsNameIgnoreCaseOrMiddleNameIgnoreCaseOrLastNameIgnoreCaseOrSecondLastNameIgnoreCase(
            String firstsName, String middleName, String lastName, String secondLastName);

    List<User> findByNamesIgnoreCase(String firstsName, String middleName, String lastName, String secondLastName);

}
