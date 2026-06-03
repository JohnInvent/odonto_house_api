package com.odonto.house.odonto_house_api.controller;

import com.odonto.house.odonto_house_api.model.Cita;
import com.odonto.house.odonto_house_api.model.Doctor;
import com.odonto.house.odonto_house_api.model.Paciente;
import com.odonto.house.odonto_house_api.repository.CitaRepository;
import com.odonto.house.odonto_house_api.repository.DoctorRepository;
import com.odonto.house.odonto_house_api.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final DoctorRepository doctorRepository;

    public CitaController(CitaRepository citaRepository,
                         PacienteRepository pacienteRepository,
                         DoctorRepository doctorRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public List<Cita> getAll() {
        return citaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cita> getById(@PathVariable Long id) {
        return citaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cita> create(@RequestBody Cita cita) {
        assignPaciente(cita);
        assignDoctor(cita);
        Cita saved = citaRepository.save(cita);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cita> update(@PathVariable Long id, @RequestBody Cita cita) {
        return citaRepository.findById(id)
                .map(existing -> {
                    assignPaciente(cita);
                    assignDoctor(cita);
                    existing.setPaciente(cita.getPaciente());
                    existing.setDoctor(cita.getDoctor());
                    existing.setFecha(cita.getFecha());
                    existing.setHora(cita.getHora());
                    existing.setEstado(cita.getEstado());
                    existing.setMotivo(cita.getMotivo());
                    existing.setObservaciones(cita.getObservaciones());
                    return ResponseEntity.ok(citaRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return citaRepository.findById(id)
                .map(cita -> {
                    citaRepository.delete(cita);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private void assignPaciente(Cita cita) {
        if (cita.getPaciente() == null || cita.getPaciente().getId() == null) {
            throw new IllegalArgumentException("Cita must have a valid paciente id");
        }
        Paciente paciente = pacienteRepository.findById(cita.getPaciente().getId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente not found: " + cita.getPaciente().getId()));
        cita.setPaciente(paciente);
    }

    private void assignDoctor(Cita cita) {
        if (cita.getDoctor() == null || cita.getDoctor().getId() == null) {
            throw new IllegalArgumentException("Cita must have a valid doctor id");
        }
        Doctor doctor = doctorRepository.findById(cita.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + cita.getDoctor().getId()));
        cita.setDoctor(doctor);
    }
}
