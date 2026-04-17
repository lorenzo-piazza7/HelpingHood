package com.example.helpinghood.repository;

import com.example.helpinghood.model.AnnuncioTag;
import com.example.helpinghood.model.AnnuncioTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnuncioTagRepository extends JpaRepository<AnnuncioTag, AnnuncioTagId> {

    List<AnnuncioTag> findByIdAnnuncioId(Long annuncioId);
}
