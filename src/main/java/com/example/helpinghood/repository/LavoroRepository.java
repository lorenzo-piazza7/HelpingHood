package com.example.helpinghood.repository;

import com.example.helpinghood.model.Lavoro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LavoroRepository extends JpaRepository<Lavoro, Long> {

    List<Lavoro> findByGiovaneIdOrderByCreatedAtDesc(Long giovaneId);

    List<Lavoro> findByRichiedenteIdOrderByCreatedAtDesc(Long richiedenteId);
}
