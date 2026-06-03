package com.odonto.house.odonto_house_api.repository;

import com.odonto.house.odonto_house_api.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
}
