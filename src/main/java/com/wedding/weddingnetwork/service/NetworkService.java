package com.wedding.weddingnetwork.service;

import com.wedding.weddingnetwork.dto.*;
import com.wedding.weddingnetwork.entity.Location;
import com.wedding.weddingnetwork.entity.Marriage;
import com.wedding.weddingnetwork.entity.Person;
import com.wedding.weddingnetwork.exception.BadRequestException;
import com.wedding.weddingnetwork.exception.ResourceNotFoundException;
import com.wedding.weddingnetwork.repository.LocationRepository;
import com.wedding.weddingnetwork.repository.MarriageRepository;
import com.wedding.weddingnetwork.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkService {

    private final PersonRepository personRepository;
    private final MarriageRepository marriageRepository;
    private final LocationRepository locationRepository;

    // --- Person Management ---

    public Person getPersonById(Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personId));
    }

    public Page<Person> getAllPersons(int page, int size) {
        int validPage = Math.max(page, 0);
        int validSize = (size <= 0) ? 20 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(validPage, validSize, Sort.by("personId").ascending());
        return personRepository.findAll(pageable);
    }

    public Person addPerson(CreatePersonRequest req) {
        validateParentingAndAncestry(null, req.getFatherId(), req.getMotherId());

        Person person = Person.builder()
                .fullName(req.getFullName().trim())
                .gender(req.getGender() != null ? req.getGender().toUpperCase() : null)
                .phoneNumber(req.getPhoneNumber() != null ? req.getPhoneNumber().trim() : null)
                .whatsappOptIn(req.getWhatsappOptIn() != null ? req.getWhatsappOptIn() : true)
                .birthYear(req.getBirthYear())
                .isAlive(req.getIsAlive() != null ? req.getIsAlive() : true)
                .fatherId(req.getFatherId())
                .motherId(req.getMotherId())
                .hubId(req.getHubId())
                .nativeLocationId(req.getNativeLocationId())
                .currentLocationId(req.getCurrentLocationId())
                .createdAt(LocalDateTime.now())
                .build();

        return personRepository.save(person);
    }

    public Person updatePerson(Long personId, UpdatePersonRequest req) {
        Person person = getPersonById(personId);
        validateParentingAndAncestry(personId, req.getFatherId(), req.getMotherId());

        person.setFullName(req.getFullName().trim());
        if (req.getGender() != null) {
            person.setGender(req.getGender().toUpperCase());
        }
        person.setPhoneNumber(req.getPhoneNumber() != null ? req.getPhoneNumber().trim() : null);
        if (req.getWhatsappOptIn() != null) {
            person.setWhatsappOptIn(req.getWhatsappOptIn());
        }
        person.setBirthYear(req.getBirthYear());
        if (req.getIsAlive() != null) {
            person.setIsAlive(req.getIsAlive());
        }
        person.setFatherId(req.getFatherId());
        person.setMotherId(req.getMotherId());
        person.setHubId(req.getHubId());
        person.setNativeLocationId(req.getNativeLocationId());
        person.setCurrentLocationId(req.getCurrentLocationId());

        return personRepository.save(person);
    }

    // --- Location Management ---

    public Location getLocationById(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + locationId));
    }

    public Page<Location> getAllLocations(int page, int size) {
        int validPage = Math.max(page, 0);
        int validSize = (size <= 0) ? 20 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(validPage, validSize, Sort.by("locationId").ascending());
        return locationRepository.findAll(pageable);
    }

    public Location addLocation(CreateLocationRequest req) {
        Location location = Location.builder()
                .placeName(req.getPlaceName().trim())
                .villageCity(req.getVillageCity() != null ? req.getVillageCity().trim() : null)
                .tehsil(req.getTehsil() != null ? req.getTehsil().trim() : null)
                .district(req.getDistrict() != null ? req.getDistrict().trim() : null)
                .state(req.getState() != null ? req.getState().trim() : "Uttar Pradesh")
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .googlePlaceId(req.getGooglePlaceId())
                .build();

        return locationRepository.save(location);
    }

    public Location updateLocation(Long locationId, UpdateLocationRequest req) {
        Location location = getLocationById(locationId);

        location.setPlaceName(req.getPlaceName().trim());
        location.setVillageCity(req.getVillageCity() != null ? req.getVillageCity().trim() : null);
        location.setTehsil(req.getTehsil() != null ? req.getTehsil().trim() : null);
        location.setDistrict(req.getDistrict() != null ? req.getDistrict().trim() : null);
        if (req.getState() != null) {
            location.setState(req.getState().trim());
        }
        location.setLatitude(req.getLatitude());
        location.setLongitude(req.getLongitude());
        location.setGooglePlaceId(req.getGooglePlaceId());

        return locationRepository.save(location);
    }

    // --- Marriage Management ---

    public Marriage getMarriageById(Long marriageId) {
        return marriageRepository.findById(marriageId)
                .orElseThrow(() -> new ResourceNotFoundException("Marriage not found with ID: " + marriageId));
    }

    public Marriage addMarriage(CreateMarriageRequest req) {
        validateMarriageIntegrity(null, req.getHusbandId(), req.getWifeId(), req.getStatus());

        Marriage marriage = Marriage.builder()
                .husbandId(req.getHusbandId())
                .wifeId(req.getWifeId())
                .weddingYear(req.getWeddingYear())
                .weddingLocationId(req.getWeddingLocationId())
                .inLawsLocationId(req.getInLawsLocationId())
                .status(req.getStatus() != null ? req.getStatus().toUpperCase() : "ACTIVE")
                .build();

        return marriageRepository.save(marriage);
    }

    public Marriage updateMarriage(Long marriageId, UpdateMarriageRequest req) {
        Marriage marriage = getMarriageById(marriageId);
        validateMarriageIntegrity(marriageId, req.getHusbandId(), req.getWifeId(), req.getStatus());

        marriage.setHusbandId(req.getHusbandId());
        marriage.setWifeId(req.getWifeId());
        marriage.setWeddingYear(req.getWeddingYear());
        marriage.setWeddingLocationId(req.getWeddingLocationId());
        marriage.setInLawsLocationId(req.getInLawsLocationId());
        if (req.getStatus() != null) {
            marriage.setStatus(req.getStatus().toUpperCase());
        }

        return marriageRepository.save(marriage);
    }

    // --- Upgraded Search API ---

    public List<PersonRepository.PersonSearchResult> searchPersons(
            String name, String district, String village, int page, int size) {
        int validPage = Math.max(page, 0);
        int validSize = (size <= 0) ? 25 : Math.min(size, 100);
        int offset = validPage * validSize;

        String queryName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        String queryDistrict = (district != null && !district.trim().isEmpty()) ? district.trim() : null;
        String queryVillage = (village != null && !village.trim().isEmpty()) ? village.trim() : null;

        return personRepository.searchPersons(queryName, queryDistrict, queryVillage, validSize, offset);
    }

    // --- 1. Family Tree Node Details (Eliminated N+1, Batched Parents & Spouses, Added Siblings) ---

    public Map<String, Object> getPersonTree(Long personId) {
        Person person = getPersonById(personId);

        List<Person> children = personRepository.findByFatherIdOrMotherId(personId, personId);
        List<Marriage> marriages = marriageRepository.findByHusbandIdOrWifeId(personId, personId);

        // Collect all spouse IDs
        Set<Long> spouseIds = new LinkedHashSet<>();
        for (Marriage m : marriages) {
            Long spouseId = m.getHusbandId().equals(personId) ? m.getWifeId() : m.getHusbandId();
            if (spouseId != null) {
                spouseIds.add(spouseId);
            }
        }

        // Batch fetch parents and all spouses in a SINGLE query (eliminates N+1)
        Set<Long> idsToFetch = new HashSet<>(spouseIds);
        if (person.getFatherId() != null) idsToFetch.add(person.getFatherId());
        if (person.getMotherId() != null) idsToFetch.add(person.getMotherId());

        Map<Long, Person> personMap = idsToFetch.isEmpty()
                ? Collections.emptyMap()
                : personRepository.findAllById(idsToFetch).stream()
                .collect(Collectors.toMap(Person::getPersonId, Function.identity()));

        Person father = person.getFatherId() != null ? personMap.get(person.getFatherId()) : null;
        Person mother = person.getMotherId() != null ? personMap.get(person.getMotherId()) : null;

        List<Person> spouses = spouseIds.stream()
                .map(personMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Derive siblings (brothers and sisters sharing same father or mother, excluding self)
        List<Person> allSiblings = Collections.emptyList();
        if (person.getFatherId() != null || person.getMotherId() != null) {
            allSiblings = personRepository.findSiblings(person.getFatherId(), person.getMotherId(), personId);
        }

        List<Person> brothers = allSiblings.stream()
                .filter(s -> "MALE".equalsIgnoreCase(s.getGender()))
                .collect(Collectors.toList());

        List<Person> sisters = allSiblings.stream()
                .filter(s -> "FEMALE".equalsIgnoreCase(s.getGender()))
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("person", person);
        result.put("father", father);
        result.put("mother", mother);
        result.put("spouses", spouses);
        result.put("brothers", brothers);
        result.put("sisters", sisters);
        result.put("siblingsCount", allSiblings.size());
        result.put("childrenCount", children.size());
        result.put("children", children);
        return result;
    }

    // --- Smart Link or Create Relative ---

    @Transactional
    public Map<String, Object> linkOrCreateRelative(Long personId, LinkRelativeRequest req) {
        Person user = getPersonById(personId);

        Person relative;
        if (req.getExistingRelativeId() != null) {
            if (req.getExistingRelativeId().equals(personId)) {
                throw new BadRequestException("A person cannot be linked as their own relative");
            }
            relative = getPersonById(req.getExistingRelativeId());
        } else {
            if (req.getFullName() == null || req.getFullName().trim().isEmpty()) {
                throw new BadRequestException("Full name is required when creating a new relative");
            }
            String relType = req.getRelationType().toUpperCase().trim();
            String gender;
            if (relType.equals("FATHER") || relType.equals("BROTHER")) {
                gender = "MALE";
                   } else if (relType.equals("MOTHER") || relType.equals("SISTER")) {
                 gender = "FEMALE";
                   } else {
                   gender = (req.getGender() != null && !req.getGender().trim().isEmpty())
                          ? req.getGender().toUpperCase().trim()
                         : "MALE";
                           }

                           relative = Person.builder()
                        .fullName(req.getFullName().trim())
                        .gender(gender)
                          .birthYear(req.getBirthYear())
                    .nativeLocationId(user.getNativeLocationId())
                    .currentLocationId(user.getCurrentLocationId())
                    .whatsappOptIn(false)
                    .isAlive(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            relative = personRepository.save(relative);
        }

        String relationType = req.getRelationType().toUpperCase().trim();
        switch (relationType) {
            case "FATHER":
                validateParentingAndAncestry(user.getPersonId(), relative.getPersonId(), user.getMotherId());
                user.setFatherId(relative.getPersonId());
                personRepository.save(user);
                break;

            case "MOTHER":
                validateParentingAndAncestry(user.getPersonId(), user.getFatherId(), relative.getPersonId());
                user.setMotherId(relative.getPersonId());
                personRepository.save(user);
                break;

            case "BROTHER":
            case "SISTER":
            case "SIBLING":
                if (relative.getPersonId().equals(user.getPersonId())) {
                    throw new BadRequestException("A person cannot be their own sibling");
                }
                // Sync fatherId and motherId between siblings
                if (user.getFatherId() != null && relative.getFatherId() == null) {
                    relative.setFatherId(user.getFatherId());
                } else if (user.getFatherId() == null && relative.getFatherId() != null) {
                    user.setFatherId(relative.getFatherId());
                }

                if (user.getMotherId() != null && relative.getMotherId() == null) {
                    relative.setMotherId(user.getMotherId());
                } else if (user.getMotherId() == null && relative.getMotherId() != null) {
                    user.setMotherId(relative.getMotherId());
                }

                // Sync hubId if available
                if (user.getHubId() != null && relative.getHubId() == null) {
                    relative.setHubId(user.getHubId());
                } else if (user.getHubId() == null && relative.getHubId() != null) {
                    user.setHubId(relative.getHubId());
                }

                personRepository.save(relative);
                personRepository.save(user);
                break;

            default:
                throw new BadRequestException("Unsupported relation type: " + relationType);
        }

        // Return updated tree so client can immediately redraw
        return getPersonTree(personId);
    }

    // --- 2. Map Bounding-Box API (Guarded Coordinates & Capped at 500) ---

    public List<MarriageRepository.MarriageMapPin> getMarriagesInBounds(
            BigDecimal minLat, BigDecimal maxLat, BigDecimal minLng, BigDecimal maxLng) {
        if (minLat == null || maxLat == null || minLng == null || maxLng == null) {
            throw new BadRequestException("Bounding box parameters (minLat, maxLat, minLng, maxLng) are all required");
        }
        if (minLat.compareTo(maxLat) > 0) {
            throw new BadRequestException("minLat (" + minLat + ") cannot be greater than maxLat (" + maxLat + ")");
        }
        if (minLng.compareTo(maxLng) > 0) {
            throw new BadRequestException("minLng (" + minLng + ") cannot be greater than maxLng (" + maxLng + ")");
        }
        return marriageRepository.findMarriagesInBounds(minLat, maxLat, minLng, maxLng);
    }

    // --- 3. Shortest-Path Relation Finder (BFS with Projections & Memory Optimization) ---

    public List<String> findRelationPath(Long fromId, Long toId) {
        if (fromId == null || toId == null) {
            throw new BadRequestException("fromId and toId must not be null");
        }
        if (fromId.equals(toId)) {
            return List.of("Source and destination are the same person");
        }

        // Verify both persons exist
        if (!personRepository.existsById(fromId)) {
            throw new ResourceNotFoundException("Source person not found with ID: " + fromId);
        }
        if (!personRepository.existsById(toId)) {
            throw new ResourceNotFoundException("Target person not found with ID: " + toId);
        }

        // Memory optimization: fetch lightweight projections instead of full entities
        List<PersonRepository.PersonRelationProjection> persons = personRepository.findAllRelationProjections();
        List<MarriageRepository.MarriageRelationProjection> marriages = marriageRepository.findAllRelationProjections();

        Map<Long, String> names = new HashMap<>();
        Map<Long, List<Edge>> graph = new HashMap<>();

        for (PersonRepository.PersonRelationProjection p : persons) {
            names.put(p.getPersonId(), p.getFullName());
            graph.computeIfAbsent(p.getPersonId(), k -> new ArrayList<>());
        }

        for (PersonRepository.PersonRelationProjection p : persons) {
            Long pId = p.getPersonId();
            if (p.getFatherId() != null) {
                graph.computeIfAbsent(pId, k -> new ArrayList<>()).add(new Edge(p.getFatherId(), "Father"));
                graph.computeIfAbsent(p.getFatherId(), k -> new ArrayList<>()).add(new Edge(pId, "Child"));
            }
            if (p.getMotherId() != null) {
                graph.computeIfAbsent(pId, k -> new ArrayList<>()).add(new Edge(p.getMotherId(), "Mother"));
                graph.computeIfAbsent(p.getMotherId(), k -> new ArrayList<>()).add(new Edge(pId, "Child"));
            }
        }

        for (MarriageRepository.MarriageRelationProjection m : marriages) {
            if (m.getHusbandId() != null && m.getWifeId() != null) {
                graph.computeIfAbsent(m.getHusbandId(), k -> new ArrayList<>()).add(new Edge(m.getWifeId(), "Wife"));
                graph.computeIfAbsent(m.getWifeId(), k -> new ArrayList<>()).add(new Edge(m.getHusbandId(), "Husband"));
            }
        }

        // BFS Traversal
        Queue<Long> queue = new LinkedList<>();
        Map<Long, Long> parentNode = new HashMap<>();
        Map<Long, String> relationEdge = new HashMap<>();
        Set<Long> visited = new HashSet<>();

        queue.add(fromId);
        visited.add(fromId);

        boolean pathFound = false;
        while (!queue.isEmpty()) {
            Long current = queue.poll();
            if (current.equals(toId)) {
                pathFound = true;
                break;
            }

            for (Edge edge : graph.getOrDefault(current, Collections.emptyList())) {
                if (visited.add(edge.targetId())) {
                    parentNode.put(edge.targetId(), current);
                    relationEdge.put(edge.targetId(), edge.relation());
                    queue.add(edge.targetId());
                }
            }
        }

        if (!pathFound) {
            return List.of("Koi direct relation nahi mila");
        }

        // Path Reconstruction
        List<String> path = new ArrayList<>();
        Long curr = toId;
        while (!curr.equals(fromId)) {
            Long prev = parentNode.get(curr);
            String fromName = names.getOrDefault(prev, "ID:" + prev);
            String toName = names.getOrDefault(curr, "ID:" + curr);
            path.add(fromName + " --(" + relationEdge.get(curr) + ")--> " + toName);
            curr = prev;
        }
        Collections.reverse(path);
        return path;
    }

    // --- Validation Guards ---

    private void validateParentingAndAncestry(Long personId, Long fatherId, Long motherId) {
        if (fatherId != null) {
            if (personId != null && personId.equals(fatherId)) {
                throw new BadRequestException("Self-parenting violation: Person cannot be their own father");
            }
            if (!personRepository.existsById(fatherId)) {
                throw new ResourceNotFoundException("Father not found with ID: " + fatherId);
            }
        }

        if (motherId != null) {
            if (personId != null && personId.equals(motherId)) {
                throw new BadRequestException("Self-parenting violation: Person cannot be their own mother");
            }
            if (!personRepository.existsById(motherId)) {
                throw new ResourceNotFoundException("Mother not found with ID: " + motherId);
            }
        }

        // Circular ancestry guard: walk ancestors up to 20 levels
        if (personId != null && (fatherId != null || motherId != null)) {
            Set<Long> visited = new HashSet<>();
            Queue<Long> queue = new LinkedList<>();
            if (fatherId != null) queue.add(fatherId);
            if (motherId != null) queue.add(motherId);

            int depth = 0;
            while (!queue.isEmpty() && depth < 20) {
                int levelSize = queue.size();
                for (int i = 0; i < levelSize; i++) {
                    Long currentId = queue.poll();
                    if (currentId == null) continue;
                    if (currentId.equals(personId)) {
                        throw new BadRequestException("Circular ancestry detected: Person " + personId + " cannot be an ancestor of themselves");
                    }
                    if (visited.add(currentId)) {
                        Person ancestor = personRepository.findById(currentId).orElse(null);
                        if (ancestor != null) {
                            if (ancestor.getFatherId() != null) queue.add(ancestor.getFatherId());
                            if (ancestor.getMotherId() != null) queue.add(ancestor.getMotherId());
                        }
                    }
                }
                depth++;
            }
        }
    }

    private void validateMarriageIntegrity(Long marriageId, Long husbandId, Long wifeId, String status) {
        if (husbandId.equals(wifeId)) {
            throw new BadRequestException("Self-marriage violation: Husband and wife cannot be the same person");
        }
        if (!personRepository.existsById(husbandId)) {
            throw new ResourceNotFoundException("Husband not found with ID: " + husbandId);
        }
        if (!personRepository.existsById(wifeId)) {
            throw new ResourceNotFoundException("Wife not found with ID: " + wifeId);
        }

        String effectiveStatus = status != null ? status.toUpperCase() : "ACTIVE";
        if ("ACTIVE".equals(effectiveStatus)) {
            boolean exists = marriageRepository.existsActiveMarriage(husbandId, wifeId, marriageId);
            if (exists) {
                throw new BadRequestException("An active marriage already exists between person " + husbandId + " and person " + wifeId);
            }
        }
    }

    private record Edge(Long targetId, String relation) {}
}
