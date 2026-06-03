package com.odonto.house.odonto_house_api.controller;

import com.odonto.house.odonto_house_api.model.Doctor;
import com.odonto.house.odonto_house_api.model.User;
import com.odonto.house.odonto_house_api.repository.DoctorRepository;
import com.odonto.house.odonto_house_api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctores")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorController(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getById(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Doctor> create(@RequestBody Doctor doctor) {
        assignUser(doctor);
        Doctor saved = doctorRepository.save(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> update(@PathVariable Long id, @RequestBody Doctor doctor) {
        return doctorRepository.findById(id)
                .map(existing -> {
                    assignUser(doctor);
                    existing.setUser(doctor.getUser());
                    existing.setEspecialidad(doctor.getEspecialidad());
                    existing.setLicenciaProfesional(doctor.getLicenciaProfesional());
                    return ResponseEntity.ok(doctorRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .map(doctor -> {
                    doctorRepository.delete(doctor);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private void assignUser(Doctor doctor) {
        if (doctor.getUser() == null || doctor.getUser().getId() == null) {
            throw new IllegalArgumentException("Doctor must have a valid user id");
        }
        User user = userRepository.findById(doctor.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + doctor.getUser().getId()));
        doctor.setUser(user);
    }
}
