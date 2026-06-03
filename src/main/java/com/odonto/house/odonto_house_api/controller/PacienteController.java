package com.odonto.house.odonto_house_api.controller;

import com.odonto.house.odonto_house_api.model.Paciente;
import com.odonto.house.odonto_house_api.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteRepository pacienteRepository;

    public PacienteController(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public List<Paciente> getAll() {
        return pacienteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> getById(@PathVariable Long id) {
        return pacienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Paciente> create(@RequestBody Paciente paciente) {
        Paciente saved = pacienteRepository.save(paciente);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> update(@PathVariable Long id, @RequestBody Paciente paciente) {
        return pacienteRepository.findById(id)
                .map(existing -> {
                    existing.setNombres(paciente.getNombres());
                    existing.setApellidos(paciente.getApellidos());
                    existing.setDocumento(paciente.getDocumento());
                    existing.setFechaNacimiento(paciente.getFechaNacimiento());
                    existing.setGenero(paciente.getGenero());
                    existing.setTelefono(paciente.getTelefono());
                    existing.setEmail(paciente.getEmail());
                    existing.setDireccion(paciente.getDireccion());
                    existing.setContactoEmergencia(paciente.getContactoEmergencia());
                    existing.setActivo(paciente.getActivo());
                    return ResponseEntity.ok(pacienteRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return pacienteRepository.findById(id)
                .map(paciente -> {
                    pacienteRepository.delete(paciente);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
