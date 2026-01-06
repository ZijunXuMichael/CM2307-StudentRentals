package com.studentrentals.repository;

import com.studentrentals.model.property.Property;

import java.util.*;

public class PropertyRepository {

    private final Map<String, Property> byId = new HashMap<>();
    private final Map<String, List<String>> propertyIdsByOwnerId = new HashMap<>();

    public void save(Property property) {
        byId.put(property.getPropertyId(), property);

        propertyIdsByOwnerId
                .computeIfAbsent(property.getOwnerId(), k -> new ArrayList<>())
                .add(property.getPropertyId());
    }

    public Optional<Property> findById(String propertyId) {
        return Optional.ofNullable(byId.get(propertyId));
    }

    public List<Property> findByOwnerId(String ownerId) {
        List<String> ids = propertyIdsByOwnerId.getOrDefault(ownerId, List.of());
        List<Property> result = new ArrayList<>();
        for (String id : ids) {
            Property p = byId.get(id);
            if (p != null) result.add(p);
        }
        return result;
    }

    public void delete(String ownerId, String propertyId) {
        Property removed = byId.remove(propertyId);
        if (removed == null) return;

        List<String> ids = propertyIdsByOwnerId.get(ownerId);
        if (ids != null) {
            ids.removeIf(id -> id.equals(propertyId));
            if (ids.isEmpty()) propertyIdsByOwnerId.remove(ownerId);
        }
    }
}
