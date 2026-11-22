package com.wedding.weddingnetwork.repository;

import com.wedding.weddingnetwork.entity.Marriage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface MarriageRepository extends JpaRepository<Marriage, Long> {

    List<Marriage> findByHusbandIdOrWifeId(Long husbandId, Long wifeId);

    // Map Bounding-Box Query (Sirf screen par dikhne wale area ke pins)
    @Query(value = """
        SELECT 
            m.marriage_id AS marriageId,
            h.full_name AS husbandName,
            w.full_name AS wifeName,
            m.wedding_year AS weddingYear,
            l.place_name AS placeName,
            l.district AS district,
            l.latitude AS latitude,
            l.longitude AS longitude
        FROM marriages m
        JOIN persons h ON m.husband_id = h.person_id
        JOIN persons w ON m.wife_id = w.person_id
        JOIN locations l ON m.wedding_location_id = l.location_id
        WHERE l.latitude BETWEEN :minLat AND :maxLat
          AND l.longitude BETWEEN :minLng AND :maxLng
        """, nativeQuery = true)
    List<MarriageMapPin> findMarriagesInBounds(
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng
    );

    interface MarriageMapPin {
        Long getMarriageId();
        String getHusbandName();
        String getWifeName();
        Short getWeddingYear();
        String getPlaceName();
        String getDistrict();
        BigDecimal getLatitude();
        BigDecimal getLongitude();
    }
}