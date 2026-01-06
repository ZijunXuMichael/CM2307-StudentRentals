package com.studentrentals.model.booking;

import java.time.LocalDate;

public class Booking {
    private final String bookingId;
    private final String roomId;
    private final String ownerId;
    private final String studentId;

    private final LocalDate startDate;
    private final LocalDate endDate;

    private BookingStatus status;

    public Booking(String bookingId, String roomId, String ownerId, String studentId,
                   LocalDate startDate, LocalDate endDate, BookingStatus status) {
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.ownerId = ownerId;
        this.studentId = studentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getBookingId() { return bookingId; }
    public String getRoomId() { return roomId; }
    public String getOwnerId() { return ownerId; }
    public String getStudentId() { return studentId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    // true if [aStart, aEnd] overlaps [bStart, bEnd]
    public boolean overlaps(LocalDate otherStart, LocalDate otherEnd) {
        // overlap exists unless one ends before the other starts
        return !(endDate.isBefore(otherStart) || otherEnd.isBefore(startDate));
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + bookingId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", start=" + startDate +
                ", end=" + endDate +
                ", status=" + status +
                '}';
    }
}
