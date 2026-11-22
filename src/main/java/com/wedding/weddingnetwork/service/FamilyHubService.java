package com.wedding.weddingnetwork.service;

import com.wedding.weddingnetwork.dto.CreateFamilyHubRequest;
import com.wedding.weddingnetwork.dto.UpdateFamilyHubRequest;
import com.wedding.weddingnetwork.entity.FamilyHub;
import com.wedding.weddingnetwork.entity.Person;
import com.wedding.weddingnetwork.exception.ResourceNotFoundException;
import com.wedding.weddingnetwork.repository.FamilyHubRepository;
import com.wedding.weddingnetwork.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyHubService {

    private final FamilyHubRepository familyHubRepository;
    private final PersonRepository personRepository;

    public FamilyHub createFamilyHub(CreateFamilyHubRequest req) {
        FamilyHub hub = FamilyHub.builder()
                .familyName(req.getFamilyName())
                .surname(req.getSurname())
                .nativeLocationId(req.getNativeLocationId())
                .headPersonId(req.getHeadPersonId())
                .createdAt(LocalDateTime.now())
                .build();
        return familyHubRepository.save(hub);
    }

    public Page<FamilyHub> getFamilyHubs(int page, int size) {
        int validPage = Math.max(page, 0);
        int validSize = (size <= 0) ? 20 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(validPage, validSize, Sort.by("familyName").ascending());
        return familyHubRepository.findAll(pageable);
    }

    public FamilyHub getFamilyHubById(Long hubId) {
        return familyHubRepository.findById(hubId)
                .orElseThrow(() -> new ResourceNotFoundException("FamilyHub not found with ID: " + hubId));
    }

    public FamilyHub updateFamilyHub(Long hubId, UpdateFamilyHubRequest req) {
        FamilyHub hub = getFamilyHubById(hubId);
        hub.setFamilyName(req.getFamilyName());
        hub.setSurname(req.getSurname());
        hub.setNativeLocationId(req.getNativeLocationId());
        hub.setHeadPersonId(req.getHeadPersonId());
        return familyHubRepository.save(hub);
    }

    public List<Person> getHubMembers(Long hubId) {
        if (!familyHubRepository.existsById(hubId)) {
            throw new ResourceNotFoundException("FamilyHub not found with ID: " + hubId);
        }
        return personRepository.findByHubId(hubId);
    }
}

