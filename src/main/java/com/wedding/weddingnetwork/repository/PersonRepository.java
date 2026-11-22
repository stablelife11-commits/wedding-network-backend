package com.wedding.weddingnetwork.repository;

import com.wedding.weddingnetwork.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByPhoneNumber(String phoneNumber);

    List<Person> findByFatherIdOrMotherId(Long fatherId, Long motherId);

    List<Person> findByHubId(Long hubId);

    @Query("SELECT p FROM Person p WHERE p.personId != :personId AND " +
           "((:fatherId IS NOT NULL AND p.fatherId = :fatherId) OR " +
           " (:motherId IS NOT NULL AND p.motherId = :motherId))")
    List<Person> findSiblings(
            @Param("fatherId") Long fatherId,
            @Param("motherId") Long motherId,
            @Param("personId") Long personId
    );

    @Query(value = """
            SELECT 
                p.person_id AS personId,
                p.full_name AS fullName,
                p.phone_number AS phoneNumber,
                p.birth_year AS birthYear,
                p.gender AS gender,
                COALESCE(l.village_city, '') AS villageCity,
                COALESCE(l.tehsil, '') AS tehsil,
                COALESCE(l.district, '') AS district,
                COALESCE(l.place_name, '') AS placeName
            FROM persons p
            LEFT JOIN locations l ON p.native_location_id = l.location_id
            WHERE (:name IS NULL OR :name = '' OR LOWER(p.full_name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:district IS NULL OR :district = '' OR LOWER(l.district) LIKE LOWER(CONCAT('%', :district, '%')))
              AND (:village IS NULL OR :village = '' 
                   OR LOWER(l.village_city) LIKE LOWER(CONCAT('%', :village, '%'))
                   OR LOWER(l.place_name) LIKE LOWER(CONCAT('%', :village, '%')))
            ORDER BY p.full_name ASC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<PersonSearchResult> searchPersons(
            @Param("name") String name,
            @Param("district") String district,
            @Param("village") String village,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query("SELECT p.personId AS personId, p.fullName AS fullName, p.fatherId AS fatherId, p.motherId AS motherId FROM Person p")
    List<PersonRelationProjection> findAllRelationProjections();

    interface PersonSearchResult {
        Long getPersonId();
        String getFullName();
        String getPhoneNumber();
        Short getBirthYear();
        String getGender();
        String getVillageCity();
        String getTehsil();
        String getDistrict();
        String getPlaceName();
    }

    interface PersonRelationProjection {
        Long getPersonId();
        String getFullName();
        Long getFatherId();
        Long getMotherId();
    }
}
