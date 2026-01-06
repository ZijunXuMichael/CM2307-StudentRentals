package com.studentrentals.ui;

import com.studentrentals.model.booking.BookingStatus;
import com.studentrentals.model.property.Room;
import com.studentrentals.model.property.RoomType;
import com.studentrentals.model.user.Student;
import com.studentrentals.repository.RoomRepository;
import com.studentrentals.service.BookingService;
import com.studentrentals.service.SearchCriteria;
import com.studentrentals.service.SearchService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class StudentMenu {

    private final RoomRepository roomRepository;
    private final SearchService searchService;
    private final BookingService bookingService;

    public StudentMenu(RoomRepository roomRepository, BookingService bookingService) {
        this.roomRepository = roomRepository;
        this.searchService = new SearchService(roomRepository);
        this.bookingService = bookingService;
    }

    public void run(Scanner scanner, Student student) {
        while (true) {
            System.out.println("=== Student Menu ===");
            System.out.println("Logged in as: " + student.getName() + " (" + student.getRole() + ")");
            System.out.println("1) Search rooms");
            System.out.println("2) View room details by ID");
            System.out.println("3) Request booking");
            System.out.println("4) View my bookings");
            System.out.println("0) Logout");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> searchRooms(scanner);
                    case "2" -> viewRoomDetails(scanner);
                    case "3" -> requestBooking(scanner, student);
                    case "4" -> viewMyBookings(student);
                    case "0" -> {
                        System.out.println("Logged out.");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            }

            System.out.println();
        }
    }

    private void searchRooms(Scanner scanner) {
        SearchCriteria criteria = readCriteria(scanner);
        List<Room> results = searchService.search(criteria);

        if (results.isEmpty()) {
            System.out.println("No rooms match your criteria.");
            return;
        }

        System.out.println("Matched rooms:");
        for (Room r : results) {
            System.out.println(" - " + r.getRoomId() + " | " + r.getType() + " | £" + r.getMonthlyRent()
                    + " | " + r.getCity()
                    + " | " + r.getAvailableFrom() + " to " + r.getAvailableTo());
        }
    }

    private void viewRoomDetails(Scanner scanner) {
        System.out.print("Room ID: ");
        String roomId = scanner.nextLine().trim();

        roomRepository.findById(roomId)
                .ifPresentOrElse(
                        r -> {
                            System.out.println("=== Room Details ===");
                            System.out.println("ID: " + r.getRoomId());
                            System.out.println("City: " + r.getCity());
                            System.out.println("Type: " + r.getType());
                            System.out.println("Monthly rent: £" + r.getMonthlyRent());
                            System.out.println("Availability: " + r.getAvailableFrom() + " to " + r.getAvailableTo());
                            System.out.println("Amenities: " + r.getAmenities());
                            System.out.println("Property ID: " + r.getPropertyId());
                        },
                        () -> System.out.println("Room not found: " + roomId)
                );
    }

    private void requestBooking(Scanner scanner, Student student) {
        System.out.print("Room ID: ");
        String roomId = scanner.nextLine().trim();

        LocalDate start = readDate(scanner, "Start date (YYYY-MM-DD): ");
        LocalDate end = readDate(scanner, "End date (YYYY-MM-DD): ");

        var booking = bookingService.requestBooking(student.getUserId(), roomId, start, end);
        System.out.println("Booking requested. ID=" + booking.getBookingId() + " status=" + booking.getStatus());
        System.out.println("Now wait for homeowner to accept/reject.");
    }

    private void viewMyBookings(Student student) {
        var bookings = bookingService.getBookingsForStudent(student.getUserId());
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }
        System.out.println("My bookings:");
        for (var b : bookings) {
            System.out.println(" - " + b);
        }
        System.out.println("Legend: " + BookingStatus.PENDING + "/" + BookingStatus.ACCEPTED + "/" + BookingStatus.REJECTED);
    }

    private SearchCriteria readCriteria(Scanner scanner) {
        System.out.println("Enter search criteria (press Enter to skip a field).");

        System.out.print("City: ");
        String city = scanner.nextLine().trim();
        if (city.isEmpty()) city = null;

        Double min = readOptionalDouble(scanner, "Min price (e.g. 400): ");
        Double max = readOptionalDouble(scanner, "Max price (e.g. 700): ");

        LocalDate start = readOptionalDate(scanner, "Move-in date (YYYY-MM-DD): ");
        LocalDate end = readOptionalDate(scanner, "Move-out date (YYYY-MM-DD): ");

        RoomType type = readOptionalRoomType(scanner);

        return new SearchCriteria(city, min, max, start, end, type);
    }

    private Double readOptionalDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return null;
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number.");
            }
        }
    }

    private LocalDate readOptionalDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return null;
            try {
                return LocalDate.parse(s);
            } catch (Exception ex) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    private RoomType readOptionalRoomType(Scanner scanner) {
        while (true) {
            System.out.print("Room type (Enter=any, 1=SINGLE, 2=DOUBLE): ");
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return null;
            if ("1".equals(s)) return RoomType.SINGLE;
            if ("2".equals(s)) return RoomType.DOUBLE;
            System.out.println("Invalid choice.");
        }
    }

    private LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception ex) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }
}
