package com.example.helpinghood.repository;

import com.example.helpinghood.model.GiovaneBadge;
import com.example.helpinghood.model.GiovaneBadgeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiovaneBadgeRepository extends JpaRepository<GiovaneBadge, GiovaneBadgeId> {

    List<GiovaneBadge> findByIdGiovaneId(Long giovaneId);
}
