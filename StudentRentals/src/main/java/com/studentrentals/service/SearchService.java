package com.studentrentals.service;

import com.studentrentals.model.property.Room;
import com.studentrentals.model.property.RoomType;
import com.studentrentals.repository.RoomRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    private final RoomRepository roomRepository;

    public SearchService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> search(SearchCriteria criteria) {
        // 1) Candidate set by city (uses repository index)
        List<Room> candidates;
        if (criteria.getCity() != null && !criteria.getCity().isBlank()) {
            candidates = roomRepository.findByCity(criteria.getCity());
        } else {
            candidates = roomRepository.findAll();
        }

        // 2) Apply filters
        List<Room> results = new ArrayList<>();
        for (Room room : candidates) {
            if (!matchesPrice(room, criteria.getMinPrice(), criteria.getMaxPrice())) continue;
            if (!matchesType(room, criteria.getRoomType())) continue;
            if (!matchesDate(room, criteria.getStartDate(), criteria.getEndDate())) continue;
            results.add(room);
        }
        return results;
    }

    private boolean matchesPrice(Room room, Double min, Double max) {
        double rent = room.getMonthlyRent();
        if (min != null && rent < min) return false;
        if (max != null && rent > max) return false;
        return true;
    }

    private boolean matchesType(Room room, RoomType type) {
        if (type == null) return true;
        return room.getType() == type;
    }

    // Date filter: student needs [start,end] within room's [availableFrom, availableTo]
    private boolean matchesDate(Room room, LocalDate start, LocalDate end) {
        if (start == null || end == null) return true;
        if (end.isBefore(start)) return false;

        return !start.isBefore(room.getAvailableFrom()) && !end.isAfter(room.getAvailableTo());
    }
}
