package com.wedding.weddingnetwork.controller;

import com.wedding.weddingnetwork.dto.*;
import com.wedding.weddingnetwork.entity.Location;
import com.wedding.weddingnetwork.entity.Marriage;
import com.wedding.weddingnetwork.entity.Person;
import com.wedding.weddingnetwork.repository.MarriageRepository;
import com.wedding.weddingnetwork.repository.PersonRepository;
import com.wedding.weddingnetwork.service.NetworkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/network")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NetworkController {

    private final NetworkService networkService;

    // --- Family Tree & Visualizations ---

    @GetMapping("/tree/{personId}")
    public ResponseEntity<Map<String, Object>> getPersonTree(@PathVariable("personId") Long personId) {
        return ResponseEntity.ok(networkService.getPersonTree(personId));
    }

    @PostMapping("/person/{personId}/relative")
    public ResponseEntity<Map<String, Object>> linkOrCreateRelative(
            @PathVariable("personId") Long personId,
            @Valid @RequestBody LinkRelativeRequest request) {
        return ResponseEntity.ok(networkService.linkOrCreateRelative(personId, request));
    }

    @GetMapping("/map/marriages")
    public ResponseEntity<List<MarriageRepository.MarriageMapPin>> getMarriagesInBounds(
            @RequestParam("minLat") BigDecimal minLat,
            @RequestParam("maxLat") BigDecimal maxLat,
            @RequestParam("minLng") BigDecimal minLng,
            @RequestParam("maxLng") BigDecimal maxLng) {
        return ResponseEntity.ok(networkService.getMarriagesInBounds(minLat, maxLat, minLng, maxLng));
    }

    @GetMapping("/relation")
    public ResponseEntity<List<String>> findRelation(
            @RequestParam("fromId") Long fromId,
            @RequestParam("toId") Long toId) {
        return ResponseEntity.ok(networkService.findRelationPath(fromId, toId));
    }

    // --- Person Endpoints ---

    @GetMapping("/persons")
    public ResponseEntity<Page<Person>> getAllPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(networkService.getAllPersons(page, size));
    }

    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(networkService.getPersonById(id));
    }

    @PostMapping("/person")
    public ResponseEntity<Person> addPerson(@Valid @RequestBody CreatePersonRequest request) {
        return new ResponseEntity<>(networkService.addPerson(request), HttpStatus.CREATED);
    }

    @PutMapping("/person/{id}")
    public ResponseEntity<Person> updatePerson(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdatePersonRequest request) {
        return ResponseEntity.ok(networkService.updatePerson(id, request));
    }

    @GetMapping("/persons/search")
    public ResponseEntity<List<PersonRepository.PersonSearchResult>> searchPersons(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "village", required = false) String village,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "25") int size) {
        return ResponseEntity.ok(networkService.searchPersons(name, district, village, page, size));
    }

    // --- Location Endpoints ---

    @GetMapping("/locations")
    public ResponseEntity<Page<Location>> getAllLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(networkService.getAllLocations(page, size));
    }

    @GetMapping("/location/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(networkService.getLocationById(id));
    }

    @PostMapping("/location")
    public ResponseEntity<Location> addLocation(@Valid @RequestBody CreateLocationRequest request) {
        return new ResponseEntity<>(networkService.addLocation(request), HttpStatus.CREATED);
    }

    @PutMapping("/location/{id}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(networkService.updateLocation(id, request));
    }

    // --- Marriage Endpoints ---

    @GetMapping("/marriage/{id}")
    public ResponseEntity<Marriage> getMarriageById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(networkService.getMarriageById(id));
    }

    @PostMapping("/marriage")
    public ResponseEntity<Marriage> addMarriage(@Valid @RequestBody CreateMarriageRequest request) {
        return new ResponseEntity<>(networkService.addMarriage(request), HttpStatus.CREATED);
    }

    @PutMapping("/marriage/{id}")
    public ResponseEntity<Marriage> updateMarriage(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateMarriageRequest request) {
        return ResponseEntity.ok(networkService.updateMarriage(id, request));
    }
}
