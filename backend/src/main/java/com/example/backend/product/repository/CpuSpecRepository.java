package com.example.backend.product.repository;

import com.example.backend.product.model.spec.CpuSpec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CpuSpecRepository extends JpaRepository<CpuSpec, Long> {
}
