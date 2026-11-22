package com.wedding.weddingnetwork.repository;

import com.wedding.weddingnetwork.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}