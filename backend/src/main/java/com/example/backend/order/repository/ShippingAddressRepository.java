package com.example.backend.order.repository;

import com.example.backend.order.model.ShippingAddress;
import com.example.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    Optional<ShippingAddress> findByUser(User user);
}
