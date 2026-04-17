package com.example.helpinghood.repository;

import com.example.helpinghood.model.Disponibilita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibilitaRepository extends JpaRepository<Disponibilita, Long> {

    List<Disponibilita> findByGiovaneId(Long giovaneId);
}
