package com.odonto.house.odonto_house_api.config;

import com.odonto.house.odonto_house_api.model.Permission;
import com.odonto.house.odonto_house_api.model.Role;
import com.odonto.house.odonto_house_api.model.User;
import com.odonto.house.odonto_house_api.repository.PermissionRepository;
import com.odonto.house.odonto_house_api.repository.RoleRepository;
import com.odonto.house.odonto_house_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        initPermissions();
        initRoles();
        initAdminUser();
        log.info("Data initialization completed");
    }

    @Transactional
    private void initPermissions() {
        if (permissionRepository.count() == 0) {
            log.info("Initializing permissions...");
            permissionRepository.save(Permission.builder()
                    .nombre("READ_PACIENTES")
                    .descripcion("Read pacientes permission")
                    .build());
            permissionRepository.save(Permission.builder()
                    .nombre("WRITE_PACIENTES")
                    .descripcion("Write pacientes permission")
                    .build());
            permissionRepository.save(Permission.builder()
                    .nombre("READ_CITAS")
                    .descripcion("Read citas permission")
                    .build());
            permissionRepository.save(Permission.builder()
                    .nombre("WRITE_CITAS")
                    .descripcion("Write citas permission")
                    .build());
            permissionRepository.save(Permission.builder()
                    .nombre("READ_DOCTORES")
                    .descripcion("Read doctores permission")
                    .build());
            permissionRepository.save(Permission.builder()
                    .nombre("WRITE_DOCTORES")
                    .descripcion("Write doctores permission")
                    .build());
            log.info("Permissions initialized");
        }
    }

    @Transactional
    private void initRoles() {
        if (roleRepository.count() == 0) {
            log.info("Initializing roles...");
            
            List<Permission> allPerms = permissionRepository.findAll();
            
            // Admin role with all permissions
            Role adminRole = Role.builder()
                    .nombre("ADMIN")
                    .build();
            adminRole.setPermissions(new HashSet<>(allPerms));
            roleRepository.save(adminRole);
            
            // Doctor role
            Role doctorRole = Role.builder()
                    .nombre("DOCTOR")
                    .build();
            var doctorPerms = allPerms.stream()
                    .filter(p -> p.getNombre().contains("CITAS") || p.getNombre().contains("PACIENTES"))
                    .collect(java.util.stream.Collectors.toSet());
            doctorRole.setPermissions(doctorPerms);
            roleRepository.save(doctorRole);
            
            // Secretary role
            Role secretaryRole = Role.builder()
                    .nombre("SECRETARY")
                    .build();
            var secretaryPerms = allPerms.stream()
                    .filter(p -> p.getNombre().startsWith("READ"))
                    .collect(java.util.stream.Collectors.toSet());
            secretaryRole.setPermissions(secretaryPerms);
            roleRepository.save(secretaryRole);
            
            log.info("Roles initialized");
        }
    }

    @Transactional
    private void initAdminUser() {
        if (userRepository.count() == 0) {
            log.info("Creating default admin user...");
            
            Role adminRole = roleRepository.findByNombre("ADMIN").orElse(null);
            
            User admin = User.builder()
                    .nombre("Administrator")
                    .apellido("System")
                    .email("admin@odonto.com")
                    .password(passwordEncoder.encode("admin123"))
                    .telefono("0000000000")
                    .activo(true)
                    .build();
            
            if (adminRole != null) {
                admin.setRoles(new HashSet<>());
                admin.getRoles().add(adminRole);
            }
            
            userRepository.save(admin);
            log.info("Default admin user created: admin@odonto.com / admin123");
        }
    }
}