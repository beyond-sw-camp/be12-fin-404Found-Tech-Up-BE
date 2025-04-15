package com.example.backend.product.repository;

import com.example.backend.product.model.spec.RamSpec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RamSpecRepository extends JpaRepository<RamSpec, Integer> {
}
