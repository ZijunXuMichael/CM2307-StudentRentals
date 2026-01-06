package com.studentrentals.repository;

import com.studentrentals.model.booking.Booking;
import com.studentrentals.model.booking.BookingStatus;

import java.util.*;

public class BookingRepository {

    private final Map<String, Booking> byId = new HashMap<>();

    private final Map<String, List<String>> bookingIdsByRoomId = new HashMap<>();
    private final Map<String, List<String>> bookingIdsByOwnerId = new HashMap<>();
    private final Map<String, List<String>> bookingIdsByStudentId = new HashMap<>();

    public void save(Booking booking) {
        byId.put(booking.getBookingId(), booking);

        bookingIdsByRoomId.computeIfAbsent(booking.getRoomId(), k -> new ArrayList<>()).add(booking.getBookingId());
        bookingIdsByOwnerId.computeIfAbsent(booking.getOwnerId(), k -> new ArrayList<>()).add(booking.getBookingId());
        bookingIdsByStudentId.computeIfAbsent(booking.getStudentId(), k -> new ArrayList<>()).add(booking.getBookingId());
    }

    public Optional<Booking> findById(String bookingId) {
        return Optional.ofNullable(byId.get(bookingId));
    }

    public List<Booking> findByStudent(String studentId) {
        return materialize(bookingIdsByStudentId.getOrDefault(studentId, List.of()));
    }

    public List<Booking> findByOwner(String ownerId) {
        return materialize(bookingIdsByOwnerId.getOrDefault(ownerId, List.of()));
    }

    public List<Booking> findByOwnerAndStatus(String ownerId, BookingStatus status) {
        List<Booking> all = findByOwner(ownerId);
        List<Booking> out = new ArrayList<>();
        for (Booking b : all) if (b.getStatus() == status) out.add(b);
        return out;
    }

    public List<Booking> findByRoomAndStatus(String roomId, BookingStatus status) {
        List<Booking> all = materialize(bookingIdsByRoomId.getOrDefault(roomId, List.of()));
        List<Booking> out = new ArrayList<>();
        for (Booking b : all) if (b.getStatus() == status) out.add(b);
        return out;
    }

    private List<Booking> materialize(List<String> ids) {
        List<Booking> out = new ArrayList<>();
        for (String id : ids) {
            Booking b = byId.get(id);
            if (b != null) out.add(b);
        }
        return out;
    }
}
