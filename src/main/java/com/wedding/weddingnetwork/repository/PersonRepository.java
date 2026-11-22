package com.wedding.weddingnetwork.repository;

import com.wedding.weddingnetwork.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByFatherIdOrMotherId(Long fatherId, Long motherId);

    List<Person> findByHubId(Long hubId);

    @Query(value = """
            SELECT 
                p.person_id AS personId,
                p.full_name AS fullName,
                p.phone_number AS phoneNumber,
                p.birth_year AS birthYear,
                COALESCE(l.village_city, 'Unknown') AS villageCity,
                COALESCE(l.district, '') AS district
            FROM persons p
            LEFT JOIN locations l ON p.native_location_id = l.location_id
            WHERE (:name IS NULL OR :name = '' OR LOWER(p.full_name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:location IS NULL OR :location = '' 
                   OR LOWER(l.village_city) LIKE LOWER(CONCAT('%', :location, '%'))
                   OR LOWER(l.district) LIKE LOWER(CONCAT('%', :location, '%'))
                   OR LOWER(l.place_name) LIKE LOWER(CONCAT('%', :location, '%')))
            ORDER BY p.full_name ASC
            LIMIT 25
            """, nativeQuery = true)
    List<PersonSearchResult> searchPersons(
            @Param("name") String name,
            @Param("location") String location
    );

    interface PersonSearchResult {
        Long getPersonId();

        String getFullName();

        String getPhoneNumber();

        Short getBirthYear();

        String getVillageCity();

        String getDistrict();

    }
}