package com.wedding.weddingnetwork.service;

import com.wedding.weddingnetwork.entity.Marriage;
import com.wedding.weddingnetwork.entity.Person;
import com.wedding.weddingnetwork.repository.MarriageRepository;
import com.wedding.weddingnetwork.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NetworkService {

    private final PersonRepository personRepository;
    private final MarriageRepository marriageRepository;

    // 1. Family Tree Node Details (Person + Parents + Spouses + Children)
    public Map<String, Object> getPersonTree(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found: " + personId));

        List<Person> children = personRepository.findByFatherIdOrMotherId(personId, personId);
        List<Marriage> marriages = marriageRepository.findByHusbandIdOrWifeId(personId, personId);

        List<Person> spouses = new ArrayList<>();
        for (Marriage m : marriages) {
            Long spouseId = m.getHusbandId().equals(personId) ? m.getWifeId() : m.getHusbandId();
            personRepository.findById(spouseId).ifPresent(spouses::add);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("person", person);
        result.put("father", person.getFatherId() != null ? personRepository.findById(person.getFatherId()).orElse(null) : null);
        result.put("mother", person.getMotherId() != null ? personRepository.findById(person.getMotherId()).orElse(null) : null);
        result.put("spouses", spouses);
        result.put("childrenCount", children.size());
        result.put("children", children);
        return result;
    }

    // 2. Map Bounding-Box API
    public List<MarriageRepository.MarriageMapPin> getMarriagesInBounds(
            BigDecimal minLat, BigDecimal maxLat, BigDecimal minLng, BigDecimal maxLng) {
        return marriageRepository.findMarriagesInBounds(minLat, maxLat, minLng, maxLng);
    }

    // 3. Shortest-Path Relation Finder (BFS Algorithm)
    public List<String> findRelationPath(Long fromId, Long toId) {
        List<Person> allPersons = personRepository.findAll();
        List<Marriage> allMarriages = marriageRepository.findAll();

        Map<Long, String> names = new HashMap<>();
        Map<Long, List<Edge>> graph = new HashMap<>();

        for (Person p : allPersons) {
            names.put(p.getPersonId(), p.getFullName());
            graph.putIfAbsent(p.getPersonId(), new ArrayList<>());
        }

        for (Person p : allPersons) {
            if (p.getFatherId() != null) {
                graph.get(p.getPersonId()).add(new Edge(p.getFatherId(), "Father"));
                graph.get(p.getFatherId()).add(new Edge(p.getPersonId(), "Child"));
            }
            if (p.getMotherId() != null) {
                graph.get(p.getPersonId()).add(new Edge(p.getMotherId(), "Mother"));
                graph.get(p.getMotherId()).add(new Edge(p.getPersonId(), "Child"));
            }
        }

        for (Marriage m : allMarriages) {
            graph.get(m.getHusbandId()).add(new Edge(m.getWifeId(), "Wife"));
            graph.get(m.getWifeId()).add(new Edge(m.getHusbandId(), "Husband"));
        }

        // BFS Traversal
        Queue<Long> queue = new LinkedList<>();
        Map<Long, Long> parentNode = new HashMap<>();
        Map<Long, String> relationEdge = new HashMap<>();
        Set<Long> visited = new HashSet<>();

        queue.add(fromId);
        visited.add(fromId);

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            if (current.equals(toId)) break;

            for (Edge edge : graph.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(edge.targetId)) {
                    visited.add(edge.targetId);
                    parentNode.put(edge.targetId, current);
                    relationEdge.put(edge.targetId, edge.relation);
                    queue.add(edge.targetId);
                }
            }
        }

        if (!visited.contains(toId)) {
            return List.of("Koi direct relation nahi mila");
        }

        // Path Reconstruct karein
        List<String> path = new ArrayList<>();
        Long curr = toId;
        while (!curr.equals(fromId)) {
            Long prev = parentNode.get(curr);
            path.add(names.get(prev) + " --(" + relationEdge.get(curr) + ")--> " + names.get(curr));
            curr = prev;
        }
        Collections.reverse(path);
        return path;
    }

    private record Edge(Long targetId, String relation) {}
}