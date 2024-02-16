package com.web.store.services;

import com.web.store.entity.Role;
import com.web.store.entity.User;
import com.web.store.repository.RoleRepository;
import com.web.store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserServiceImplement implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Iterable<User> findAll() {

        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<User> findById(Long id) {

        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {

        Optional<Role> optionalRoleUser = roleRepository.findByRoleName("ROLE_USER");

        List<Role> roles = new ArrayList<>();

        optionalRoleUser.ifPresent(role -> roles.add(role));

        if (user.isAdmin()) {

            Optional<Role> optionalRoleAdmin = roleRepository.findByRoleName("ROLE_ADMIN");

//			roles.clear();

            optionalRoleAdmin.ifPresent(roles::add);
        }

        user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean existsByUsername(String username) {

        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    @Override
    @Transactional
    public List<User> findAllByOrderById() {
        return userRepository.findAllByOrderByIdAsc();
    }

    @Override
    public List<User> findByFirstsNameIgnoreCaseOrMiddleNameIgnoreCaseOrLastNameIgnoreCaseOrSecondLastNameIgnoreCase(
            String firstsName, String middleName, String lastName, String secondLastName) {
        return userRepository.findByFirstsNameIgnoreCaseOrMiddleNameIgnoreCaseOrLastNameIgnoreCaseOrSecondLastNameIgnoreCase(
                firstsName, middleName, lastName, secondLastName);
    }

    @Override
    public List<User> findByNamesIgnoreCase(String firstsName, String middleName, String lastName, String secondLastName) {
        return userRepository.findByFirstsNameIgnoreCaseOrMiddleNameIgnoreCaseOrLastNameIgnoreCaseOrSecondLastNameIgnoreCase(
                firstsName, middleName, lastName, secondLastName);
    }
}
