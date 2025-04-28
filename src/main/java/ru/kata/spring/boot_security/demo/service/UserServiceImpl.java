package ru.kata.spring.boot_security.demo.service;

import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> Hibernate.initialize(user.getRoles())); // Инициализируем роли у всех пользователей
        return users;
    }

    @Override
    @Transactional
    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            Hibernate.initialize(user.getRoles()); // Инициализация ролей
        }
        return user;
    }

    @Override
    @Transactional
    public void createUser(String username, int age, String email, String password, Set<String> roleNames) {
        Set<Role> roles = getRolesByName(roleNames);
        User user = new User();
        user.setUsername(username);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, String username, int age, String email, String password, Set<String> roleNames) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(username);
            user.setAge(age);
            user.setEmail(email);
            if (password != null && !password.isBlank()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            user.setRoles(getRolesByName(roleNames));
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User findByUserName(String username) {
        System.out.println("Searching user by username: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        Hibernate.initialize(user.getRoles());
        return user;
    }

    private Set<Role> getRolesByName(Set<String> roleNames) {
        return roleNames.stream()
                .map(roleRepository::findByName)
                .collect(Collectors.toSet());
    }
}
