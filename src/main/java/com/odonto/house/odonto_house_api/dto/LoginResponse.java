package com.odonto.house.odonto_house_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String nombre;
    private String apellido;
    private String email;
    private Set<String> roles;
    private Set<String> permissions;
}