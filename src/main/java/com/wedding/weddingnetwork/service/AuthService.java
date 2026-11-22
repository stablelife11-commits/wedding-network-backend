package com.wedding.weddingnetwork.service;

import com.wedding.weddingnetwork.dto.LoginRequest;
import com.wedding.weddingnetwork.dto.LoginResponse;
import com.wedding.weddingnetwork.entity.Person;
import com.wedding.weddingnetwork.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final PersonRepository personRepository;

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
}

