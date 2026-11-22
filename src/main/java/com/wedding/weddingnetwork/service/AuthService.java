package com.wedding.weddingnetwork.service;

import com.wedding.weddingnetwork.dto.LoginRequest;
import com.wedding.weddingnetwork.dto.LoginResponse;
import com.wedding.weddingnetwork.dto.RegisterProfileRequest;
import com.wedding.weddingnetwork.entity.Location;
import com.wedding.weddingnetwork.entity.Person;
import com.wedding.weddingnetwork.exception.BadRequestException;
import com.wedding.weddingnetwork.repository.LocationRepository;
import com.wedding.weddingnetwork.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final PersonRepository personRepository;
    private final LocationRepository locationRepository;

    public LoginResponse login(LoginRequest request) {
        String rawPhone = request.getPhoneNumber();
        String normalizedPhone = rawPhone != null ? rawPhone.trim().replaceAll("\\s+", "") : "";

        // Check by normalized phone or raw trimmed phone
        Optional<Person> personOpt = personRepository.findByPhoneNumber(normalizedPhone);
        if (personOpt.isEmpty() && rawPhone != null && !normalizedPhone.equals(rawPhone.trim())) {
            personOpt = personRepository.findByPhoneNumber(rawPhone.trim());
        }

        if (personOpt.isPresent()) {
            Person person = personOpt.get();
            // FUTURE_OTP_HOOK: Send/verify SMS OTP and issue JWT here
            log.info("Login successful for personId: {}, phone: {}", person.getPersonId(), normalizedPhone);
            return LoginResponse.builder()
                    .personId(person.getPersonId())
                    .fullName(person.getFullName())
                    .phoneNumber(person.getPhoneNumber())
                    .hubId(person.getHubId())
                    .registered(true)
                    .message("Login successful (OTP bypass enabled)")
                    .build();
        } else {
            log.info("Login failed: Phone number {} not registered", normalizedPhone);
            return LoginResponse.builder()
                    .personId(null)
                    .fullName(null)
                    .phoneNumber(normalizedPhone)
                    .hubId(null)
                    .registered(false)
                    .message("Mobile number not registered")
                    .build();
        }
    }

    @Transactional
    public LoginResponse register(RegisterProfileRequest request) {
        String rawPhone = request.getPhoneNumber();
        String normalizedPhone = rawPhone != null ? rawPhone.trim().replaceAll("\\s+", "") : "";

        if (personRepository.findByPhoneNumber(normalizedPhone).isPresent()
                || (rawPhone != null && !normalizedPhone.equals(rawPhone.trim()) && personRepository.findByPhoneNumber(rawPhone.trim()).isPresent())) {
            throw new BadRequestException("Mobile number already registered");
        }

        String district = request.getDistrict().trim();
        String village = request.getVillage().trim();
        String tehsil = (request.getTehsil() != null && !request.getTehsil().trim().isEmpty())
                ? request.getTehsil().trim()
                : null;

        // Find existing location or create a new one to prevent duplicate locations
        Location location = locationRepository.findExistingLocation(district, tehsil, village)
                .orElseGet(() -> {
                    Location newLoc = Location.builder()
                            .placeName(village)
                            .villageCity(village)
                            .tehsil(tehsil)
                            .district(district)
                            .state("Uttar Pradesh")
                            .build();
                    return locationRepository.save(newLoc);
                });

        Person person = Person.builder()
                .fullName(request.getFullName().trim())
                .phoneNumber(normalizedPhone)
                .gender(request.getGender().toUpperCase().trim())
                .birthYear(request.getBirthYear())
                .nativeLocationId(location.getLocationId())
                .currentLocationId(location.getLocationId())
                .whatsappOptIn(true)
                .isAlive(true)
                .createdAt(LocalDateTime.now())
                .build();

        Person savedPerson = personRepository.save(person);
        log.info("Profile registered successfully for personId: {}, phone: {}", savedPerson.getPersonId(), normalizedPhone);

        return LoginResponse.builder()
                .personId(savedPerson.getPersonId())
                .fullName(savedPerson.getFullName())
                .phoneNumber(savedPerson.getPhoneNumber())
                .hubId(savedPerson.getHubId())
                .registered(true)
                .message("Profile registered successfully")
                .build();
    }
}
