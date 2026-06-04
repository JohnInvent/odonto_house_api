package com.odonto.house.odonto_house_api.repository;

import com.odonto.house.odonto_house_api.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByNombre(String nombre);
}