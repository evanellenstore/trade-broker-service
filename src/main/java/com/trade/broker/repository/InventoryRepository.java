package com.trade.broker.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trade.broker.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductCode(String productCode);
}

