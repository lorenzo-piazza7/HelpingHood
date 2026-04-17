package com.example.helpinghood.repository;

import com.example.helpinghood.model.GiovaneCompetenza;
import com.example.helpinghood.model.GiovaneCompetenzaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiovaneCompetenzaRepository extends JpaRepository<GiovaneCompetenza, GiovaneCompetenzaId> {

    List<GiovaneCompetenza> findByIdGiovaneId(Long giovaneId);
}
