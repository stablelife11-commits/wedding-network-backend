package com.wedding.weddingnetwork.controller;

import com.wedding.weddingnetwork.dto.CreateFamilyHubRequest;
import com.wedding.weddingnetwork.dto.UpdateFamilyHubRequest;
import com.wedding.weddingnetwork.entity.FamilyHub;
import com.wedding.weddingnetwork.entity.Person;
import com.wedding.weddingnetwork.service.FamilyHubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/families")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FamilyHubController {

    private final FamilyHubService familyHubService;

    @PostMapping
    public ResponseEntity<FamilyHub> createFamilyHub(@Valid @RequestBody CreateFamilyHubRequest request) {
        FamilyHub hub = familyHubService.createFamilyHub(request);
        return new ResponseEntity<>(hub, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<FamilyHub>> getFamilyHubs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(familyHubService.getFamilyHubs(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilyHub> getFamilyHubById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(familyHubService.getFamilyHubById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FamilyHub> updateFamilyHub(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateFamilyHubRequest request) {
        return ResponseEntity.ok(familyHubService.updateFamilyHub(id, request));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<Person>> getHubMembers(@PathVariable("id") Long id) {
        return ResponseEntity.ok(familyHubService.getHubMembers(id));
    }
}

