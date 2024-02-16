package com.web.store.repository;

import com.web.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    List<User> findAllByOrderByIdAsc();

    List<User> findByFirstsNameIgnoreCaseOrMiddleNameIgnoreCaseOrLastNameIgnoreCaseOrSecondLastNameIgnoreCase(
            String firstsName, String middleName, String lastName, String secondLastName);

}
