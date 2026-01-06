package com.studentrentals.repository;

import com.studentrentals.model.property.Room;

import java.util.*;

public class RoomRepository {

    private final Map<String, Room> byId = new HashMap<>();
    private final Map<String, List<String>> roomIdsByCity = new HashMap<>();

    public void save(String city, Room room) {
        String key = norm(city);
        byId.put(room.getRoomId(), room);
        roomIdsByCity.computeIfAbsent(key, k -> new ArrayList<>()).add(room.getRoomId());
    }

    public Optional<Room> findById(String roomId) {
        return Optional.ofNullable(byId.get(roomId));
    }

    public List<Room> findByCity(String city) {
        List<String> ids = roomIdsByCity.getOrDefault(norm(city), List.of());
        List<Room> rooms = new ArrayList<>();
        for (String id : ids) {
            Room r = byId.get(id);
            if (r != null) rooms.add(r);
        }
        return rooms;
    }

    public List<Room> findAll() {
        return new ArrayList<>(byId.values());
    }

    public void delete(String city, String roomId) {
        byId.remove(roomId);

        List<String> ids = roomIdsByCity.get(norm(city));
        if (ids != null) {
            ids.removeIf(id -> id.equals(roomId));
            if (ids.isEmpty()) roomIdsByCity.remove(norm(city));
        }
    }

    // If you ever allow changing city, call this (not required if city fixed)
    public void moveCityIndex(String oldCity, String newCity, String roomId) {
        List<String> oldList = roomIdsByCity.get(norm(oldCity));
        if (oldList != null) {
            oldList.removeIf(id -> id.equals(roomId));
            if (oldList.isEmpty()) roomIdsByCity.remove(norm(oldCity));
        }
        roomIdsByCity.computeIfAbsent(norm(newCity), k -> new ArrayList<>()).add(roomId);
    }

    private String norm(String city) {
        return city == null ? "" : city.toLowerCase().trim();
    }
}
