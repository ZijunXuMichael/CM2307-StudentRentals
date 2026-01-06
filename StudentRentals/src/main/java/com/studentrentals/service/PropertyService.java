package com.studentrentals.service;

import com.studentrentals.model.property.Property;
import com.studentrentals.model.property.Room;
import com.studentrentals.model.property.RoomType;
import com.studentrentals.repository.PropertyRepository;
import com.studentrentals.repository.RoomRepository;
import com.studentrentals.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;

public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;

    public PropertyService(PropertyRepository propertyRepository, RoomRepository roomRepository) {
        this.propertyRepository = propertyRepository;
        this.roomRepository = roomRepository;
    }

    public Property createProperty(String ownerId, String address, String city, String description) {
        if (ownerId == null || ownerId.isBlank()) throw new IllegalArgumentException("OwnerId required");
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Address required");
        if (city == null || city.isBlank()) throw new IllegalArgumentException("City required");
        if (description == null) description = "";

        Property property = new Property(
                IdGenerator.newId("prop"),
                ownerId.trim(),
                address.trim(),
                city.trim(),
                description.trim()
        );
        propertyRepository.save(property);
        return property;
    }

    public Property updateProperty(String ownerId, String propertyId,
                                   String newAddress, String newCity, String newDescription) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (!property.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("You do not own this property");
        }

        // NOTE: changing city would require re-indexing all rooms in RoomRepository.
        // To keep things simple (and safe), we allow city update ONLY if property has no rooms.
        if (newCity != null && !newCity.isBlank() && !newCity.trim().equals(property.getCity())) {
            if (!property.getRooms().isEmpty()) {
                throw new IllegalArgumentException("Cannot change city when property has rooms (keep city fixed).");
            }
            property.setCity(newCity.trim());
        }

        if (newAddress != null && !newAddress.isBlank()) property.setAddress(newAddress.trim());
        if (newDescription != null) property.setDescription(newDescription.trim());

        return property;
    }

    public void removeProperty(String ownerId, String propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (!property.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("You do not own this property");
        }

        // remove all rooms from repository index first
        for (Room r : property.getRooms()) {
            roomRepository.delete(property.getCity(), r.getRoomId());
        }

        propertyRepository.delete(ownerId, propertyId);
    }

    public Room addRoomToProperty(String ownerId, String propertyId,
                                 RoomType type, double monthlyRent, List<String> amenities,
                                 LocalDate availableFrom, LocalDate availableTo) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (!property.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("You do not own this property");
        }

        validateRoom(type, monthlyRent, availableFrom, availableTo);

        String roomId = IdGenerator.newId("room");

        Room room = new Room(
                roomId,
                property.getPropertyId(),
                property.getOwnerId(),
                property.getCity(),
                type,
                monthlyRent,
                amenities == null ? List.of() : amenities,
                availableFrom,
                availableTo
        );

        property.addRoom(room);
        roomRepository.save(property.getCity(), room);

        return room;
    }

    public Room updateRoom(String ownerId, String propertyId, String roomId,
                           RoomType newType, Double newMonthlyRent, List<String> newAmenities,
                           LocalDate newAvailableFrom, LocalDate newAvailableTo) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (!property.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("You do not own this property");
        }

        Room room = property.findRoom(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found in this property"));

        // Apply partial updates
        if (newType != null) room.setType(newType);
        if (newMonthlyRent != null) {
            if (newMonthlyRent <= 0) throw new IllegalArgumentException("Monthly rent must be > 0");
            room.setMonthlyRent(newMonthlyRent);
        }
        if (newAmenities != null) room.setAmenities(newAmenities);

        if (newAvailableFrom != null) room.setAvailableFrom(newAvailableFrom);
        if (newAvailableTo != null) room.setAvailableTo(newAvailableTo);

        if (room.getAvailableTo().isBefore(room.getAvailableFrom())) {
            throw new IllegalArgumentException("availableTo must be after availableFrom");
        }

        // RoomRepository stores same object reference; no need to re-save unless city changes.
        return room;
    }

    public void removeRoom(String ownerId, String propertyId, String roomId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (!property.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("You do not own this property");
        }

        boolean removed = property.removeRoom(roomId);
        if (!removed) throw new IllegalArgumentException("Room not found in this property");

        roomRepository.delete(property.getCity(), roomId);
    }

    public List<Property> viewOwnerProperties(String ownerId) {
        return propertyRepository.findByOwnerId(ownerId);
    }

    private void validateRoom(RoomType type, double monthlyRent, LocalDate availableFrom, LocalDate availableTo) {
        if (type == null) throw new IllegalArgumentException("Room type required");
        if (monthlyRent <= 0) throw new IllegalArgumentException("Monthly rent must be > 0");
        if (availableFrom == null || availableTo == null) throw new IllegalArgumentException("Dates required");
        if (availableTo.isBefore(availableFrom)) throw new IllegalArgumentException("availableTo must be after availableFrom");
    }
}
