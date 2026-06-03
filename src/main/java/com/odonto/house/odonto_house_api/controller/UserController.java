package com.odonto.house.odonto_house_api.controller;

import com.odonto.house.odonto_house_api.model.Role;
import com.odonto.house.odonto_house_api.model.User;
import com.odonto.house.odonto_house_api.repository.RoleRepository;
import com.odonto.house.odonto_house_api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        if (user.getRoles() != null) {
            user.setRoles(resolveRoles(user.getRoles()));
        }
        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(user.getNombre());
                    existing.setApellido(user.getApellido());
                    existing.setEmail(user.getEmail());
                    existing.setPassword(user.getPassword());
                    existing.setTelefono(user.getTelefono());
                    existing.setActivo(user.getActivo());
                    if (user.getRoles() != null) {
                        existing.setRoles(resolveRoles(user.getRoles()));
                    }
                    return ResponseEntity.ok(userRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Set<Role> resolveRoles(Set<Role> roles) {
        return roles.stream()
                .map(role -> roleRepository.findById(role.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + role.getId())))
                .collect(Collectors.toSet());
    }
}
