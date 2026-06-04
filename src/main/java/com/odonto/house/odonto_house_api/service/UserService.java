package com.odonto.house.odonto_house_api.service;

import com.odonto.house.odonto_house_api.dto.UserRequest;
import com.odonto.house.odonto_house_api.dto.UserResponse;
import com.odonto.house.odonto_house_api.model.Role;
import com.odonto.house.odonto_house_api.model.User;
import com.odonto.house.odonto_house_api.repository.RoleRepository;
import com.odonto.house.odonto_house_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        User user = User.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        if (request.getRolesIds() != null && !request.getRolesIds().isEmpty()) {
            Set<Role> roles = resolveRoles(request.getRolesIds());
            user.setRoles(roles);
        }

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar si el nuevo email ya está en uso por otro usuario
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setEmail(request.getEmail());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        user.setTelefono(request.getTelefono());
        
        if (request.getActivo() != null) {
            user.setActivo(request.getActivo());
        }

        if (request.getRolesIds() != null) {
            Set<Role> roles = resolveRoles(request.getRolesIds());
            user.setRoles(roles);
        }

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Soft delete - desactivamos el usuario
        user.setActivo(false);
        userRepository.save(user);
    }

    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        user.setActivo(true);
        userRepository.save(user);
    }

    @Transactional
    public String generateResetToken(String email) {
        // Busca el usuario pero no revela si existe o no por seguridad
        var userOpt = userRepository.findByEmail(email);
        
        // Si el usuario no existe o no está activo, no hacemos nada
        // Esto evita ataques de enumeración de usuarios
        if (userOpt.isEmpty() || !Boolean.TRUE.equals(userOpt.get().getActivo())) {
            // Silenciosamente retornar un token dummy para no revelar información
            return "dummy-token";
        }
        
        User user = userOpt.get();
        
        // Generar token único
        String token = UUID.randomUUID().toString();
        
        // Guardar token con fecha de expiración (1 hora)
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        return token;
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        // Verificar si el token ha expirado
        if (user.getResetTokenExpiry() == null || 
            user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado");
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        
        // Limpiar token
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        userRepository.save(user);
    }

    private Set<Role> resolveRoles(Set<Long> roleIds) {
        return roleIds.stream()
                .map(id -> roleRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + id)))
                .collect(Collectors.toSet());
    }

    private UserResponse mapToResponse(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::getNombre)
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .email(user.getEmail())
                .telefono(user.getTelefono())
                .activo(user.getActivo())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}