package com.studentrentals.service;

import com.studentrentals.model.booking.Booking;
import com.studentrentals.model.booking.BookingStatus;
import com.studentrentals.repository.BookingRepository;
import com.studentrentals.repository.RoomRepository;
import com.studentrentals.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;

public class BookingService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public BookingService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    // Student requests booking -> creates PENDING if valid and not overlapping with accepted bookings
    public Booking requestBooking(String studentId, String roomId, LocalDate start, LocalDate end) {
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("StudentId required");
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("RoomId required");
        if (start == null || end == null) throw new IllegalArgumentException("Dates required");
        if (end.isBefore(start)) throw new IllegalArgumentException("End date must be after start date");

        var room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // within room availability window
        if (start.isBefore(room.getAvailableFrom()) || end.isAfter(room.getAvailableTo())) {
            throw new IllegalArgumentException("Requested dates are outside room availability");
        }

        // Prevent double booking: check overlap with ACCEPTED bookings
        List<Booking> accepted = bookingRepository.findByRoomAndStatus(roomId, BookingStatus.ACCEPTED);
        for (Booking b : accepted) {
            if (b.overlaps(start, end)) {
                throw new IllegalArgumentException("Room already booked for the requested period");
            }
        }

        Booking booking = new Booking(
                IdGenerator.newId("book"),
                roomId,
                room.getOwnerId(),
                studentId.trim(),
                start,
                end,
                BookingStatus.PENDING
        );
        bookingRepository.save(booking);
        return booking;
    }

    public List<Booking> getPendingRequestsForOwner(String ownerId) {
        if (ownerId == null || ownerId.isBlank()) throw new IllegalArgumentException("OwnerId required");
        return bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.PENDING);
    }

    public List<Booking> getBookingsForOwner(String ownerId) {
        if (ownerId == null || ownerId.isBlank()) throw new IllegalArgumentException("OwnerId required");
        return bookingRepository.findByOwner(ownerId);
    }

    public List<Booking> getBookingsForStudent(String studentId) {
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("StudentId required");
        return bookingRepository.findByStudent(studentId);
    }

    // Owner accepts/rejects a pending booking
    public Booking respondToRequest(String ownerId, String bookingId, BookingStatus decision) {
        if (decision != BookingStatus.ACCEPTED && decision != BookingStatus.REJECTED) {
            throw new IllegalArgumentException("Decision must be ACCEPTED or REJECTED");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("You are not allowed to respond to this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING bookings can be responded to");
        }

        if (decision == BookingStatus.ACCEPTED) {
            // re-check overlap at acceptance time (important for correctness)
            List<Booking> accepted = bookingRepository.findByRoomAndStatus(booking.getRoomId(), BookingStatus.ACCEPTED);
            for (Booking b : accepted) {
                if (b.overlaps(booking.getStartDate(), booking.getEndDate())) {
                    throw new IllegalArgumentException("Cannot accept: room already accepted for overlapping period");
                }
            }
        }

        booking.setStatus(decision);
        return booking;
    }
}
