package com.studentrentals.model.property;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String roomId;
    private final String propertyId;
    private final String ownerId;
    private final String city;

    private RoomType type;
    private double monthlyRent;
    private List<String> amenities;
    private LocalDate availableFrom;
    private LocalDate availableTo;

    public Room(String roomId, String propertyId, String ownerId, String city,
                RoomType type, double monthlyRent,
                List<String> amenities, LocalDate availableFrom, LocalDate availableTo) {
        this.roomId = roomId;
        this.propertyId = propertyId;
        this.ownerId = ownerId;
        this.city = city;
        this.type = type;
        this.monthlyRent = monthlyRent;
        this.amenities = new ArrayList<>(amenities);
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
    }

    public String getRoomId() { return roomId; }
    public String getPropertyId() { return propertyId; }
    public String getOwnerId() { return ownerId; }
    public String getCity() { return city; }

    public RoomType getType() { return type; }
    public double getMonthlyRent() { return monthlyRent; }
    public List<String> getAmenities() { return new ArrayList<>(amenities); }
    public LocalDate getAvailableFrom() { return availableFrom; }
    public LocalDate getAvailableTo() { return availableTo; }

    public void setType(RoomType type) { this.type = type; }
    public void setMonthlyRent(double monthlyRent) { this.monthlyRent = monthlyRent; }
    public void setAmenities(List<String> amenities) { this.amenities = new ArrayList<>(amenities); }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }
    public void setAvailableTo(LocalDate availableTo) { this.availableTo = availableTo; }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + roomId + '\'' +
                ", propertyId='" + propertyId + '\'' +
                ", type=" + type +
                ", rent=" + monthlyRent +
                ", city='" + city + '\'' +
                ", from=" + availableFrom +
                ", to=" + availableTo +
                ", amenities=" + amenities +
                '}';
    }
}
