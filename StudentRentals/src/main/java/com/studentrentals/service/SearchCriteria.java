package com.studentrentals.service;

import com.studentrentals.model.property.RoomType;

import java.time.LocalDate;

public class SearchCriteria {
    private final String city;              // nullable/blank means any
    private final Double minPrice;          // nullable means no min
    private final Double maxPrice;          // nullable means no max
    private final LocalDate startDate;      // nullable means ignore
    private final LocalDate endDate;        // nullable means ignore
    private final RoomType roomType;        // nullable means any

    public SearchCriteria(String city, Double minPrice, Double maxPrice,
                          LocalDate startDate, LocalDate endDate,
                          RoomType roomType) {
        this.city = city;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roomType = roomType;
    }

    public String getCity() { return city; }
    public Double getMinPrice() { return minPrice; }
    public Double getMaxPrice() { return maxPrice; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public RoomType getRoomType() { return roomType; }
}
