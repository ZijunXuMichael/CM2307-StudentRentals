package com.studentrentals.model.property;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Property {
    private final String propertyId;
    private final String ownerId; // Homeowner userId
    private String address;
    private String city;
    private String description;

    private final List<Room> rooms = new ArrayList<>();

    public Property(String propertyId, String ownerId, String address, String city, String description) {
        this.propertyId = propertyId;
        this.ownerId = ownerId;
        this.address = address;
        this.city = city;
        this.description = description;
    }

    public String getPropertyId() { return propertyId; }
    public String getOwnerId() { return ownerId; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getDescription() { return description; }
    public List<Room> getRooms() { return new ArrayList<>(rooms); }

    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setDescription(String description) { this.description = description; }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public Optional<Room> findRoom(String roomId) {
        for (Room r : rooms) {
            if (r.getRoomId().equals(roomId)) return Optional.of(r);
        }
        return Optional.empty();
    }

    public boolean removeRoom(String roomId) {
        return rooms.removeIf(r -> r.getRoomId().equals(roomId));
    }

    @Override
    public String toString() {
        return "Property{" +
                "id='" + propertyId + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", rooms=" + rooms.size() +
                '}';
    }
}
