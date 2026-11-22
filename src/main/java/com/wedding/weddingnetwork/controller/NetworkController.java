package com.wedding.weddingnetwork.controller;

import com.wedding.weddingnetwork.entity.Location;
import com.wedding.weddingnetwork.entity.Marriage;
import com.wedding.weddingnetwork.entity.Person;
import com.wedding.weddingnetwork.repository.LocationRepository;
import com.wedding.weddingnetwork.repository.MarriageRepository;
import com.wedding.weddingnetwork.repository.PersonRepository;
import com.wedding.weddingnetwork.service.NetworkService;
import lombok.RequiredArgsConstructor;
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
    private final PersonRepository personRepository;
    private final LocationRepository locationRepository;
    private final MarriageRepository marriageRepository;

    // --- GET APIs ---

    @GetMapping("/tree/{personId}")
    public Map<String, Object> getPersonTree(@PathVariable("personId") Long personId) {
        return networkService.getPersonTree(personId);
    }

    @GetMapping("/map/marriages")
    public List<MarriageRepository.MarriageMapPin> getMarriagesInBounds(
            @RequestParam("minLat") BigDecimal minLat,
            @RequestParam("maxLat") BigDecimal maxLat,
            @RequestParam("minLng") BigDecimal minLng,
            @RequestParam("maxLng") BigDecimal maxLng) {
        return networkService.getMarriagesInBounds(minLat, maxLat, minLng, maxLng);
    }

    @GetMapping("/relation")
    public List<String> findRelation(
            @RequestParam("fromId") Long fromId,
            @RequestParam("toId") Long toId) {
        return networkService.findRelationPath(fromId, toId);
    }

    @GetMapping("/persons")
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @GetMapping("/locations")
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    // --- POST APIs (Add New Data) ---

    @PostMapping("/person")
    public Person addPerson(@RequestBody Person person) {
        return personRepository.save(person);
    }

    @PostMapping("/location")
    public Location addLocation(@RequestBody Location location) {
        return locationRepository.save(location);
    }

    @PostMapping("/marriage")
    public Marriage addMarriage(@RequestBody Marriage marriage) {
        return marriageRepository.save(marriage);
    }

    @GetMapping("/persons/search")
    public List<PersonRepository.PersonSearchResult> searchPersons(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "location", required = false) String location) {
        return personRepository.searchPersons(name, location);
    }
}