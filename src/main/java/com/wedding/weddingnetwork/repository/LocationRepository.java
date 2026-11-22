package com.wedding.weddingnetwork.repository;

import com.wedding.weddingnetwork.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("""
        SELECT l FROM Location l 
        WHERE LOWER(TRIM(l.district)) = LOWER(TRIM(:district))
          AND LOWER(TRIM(l.villageCity)) = LOWER(TRIM(:villageCity))
          AND (
              (:tehsil IS NULL OR TRIM(:tehsil) = '') AND (l.tehsil IS NULL OR TRIM(l.tehsil) = '')
              OR (LOWER(TRIM(l.tehsil)) = LOWER(TRIM(:tehsil)))
          )
        """)
    Optional<Location> findExistingLocation(
            @Param("district") String district,
            @Param("tehsil") String tehsil,
            @Param("villageCity") String villageCity
    );

    Optional<Location> findFirstByDistrictIgnoreCaseAndTehsilIgnoreCaseAndVillageCityIgnoreCase(
            String district, String tehsil, String villageCity
    );

    Optional<Location> findFirstByDistrictIgnoreCaseAndVillageCityIgnoreCase(
            String district, String villageCity
    );
}
