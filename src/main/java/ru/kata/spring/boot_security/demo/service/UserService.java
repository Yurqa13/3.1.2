package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long id);

    void createUser(String username, int age, String email, String password, Set<String> roleNames);

    void updateUser(Long id, String username, int age, String email, String password, Set<String> roleNames);

    void deleteUser(Long id);

    User findByUserName(String username);

}
