package com.odonto.house.odonto_house_api.controller;

import com.odonto.house.odonto_house_api.dto.LoginRequest;
import com.odonto.house.odonto_house_api.dto.LoginResponse;
import com.odonto.house.odonto_house_api.model.User;
import com.odonto.house.odonto_house_api.repository.UserRepository;
import com.odonto.house.odonto_house_api.security.CustomUserDetails;
import com.odonto.house.odonto_house_api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            if (!user.getActivo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            String jwt = jwtTokenProvider.generateToken(userDetails);

            Set<String> roles = user.getRoles().stream()
                    .map(role -> role.getNombre())
                    .collect(Collectors.toSet());

            Set<String> permissions = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(permission -> permission.getNombre())
                    .collect(Collectors.toSet());

            LoginResponse response = LoginResponse.builder()
                    .token(jwt)
                    .type("Bearer")
                    .userId(user.getId())
                    .nombre(user.getNombre())
                    .apellido(user.getApellido())
                    .email(user.getEmail())
                    .roles(roles)
                    .permissions(permissions)
                    .build();

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}