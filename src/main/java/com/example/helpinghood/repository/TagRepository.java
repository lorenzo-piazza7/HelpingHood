package com.example.helpinghood.repository;

import com.example.helpinghood.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByNomeIgnoreCase(String nome);
}
