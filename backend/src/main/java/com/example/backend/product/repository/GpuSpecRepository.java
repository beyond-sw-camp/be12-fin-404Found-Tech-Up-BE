package com.example.backend.product.repository;

import com.example.backend.product.model.spec.GpuSpec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GpuSpecRepository extends JpaRepository<GpuSpec, Long> {
}
