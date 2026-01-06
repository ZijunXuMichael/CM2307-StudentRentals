package com.studentrentals.ui;

import com.studentrentals.model.booking.BookingStatus;
import com.studentrentals.model.property.RoomType;
import com.studentrentals.model.user.Homeowner;
import com.studentrentals.service.BookingService;
import com.studentrentals.service.PropertyService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HomeownerMenu {

    private final PropertyService propertyService;
    private final BookingService bookingService;

    public HomeownerMenu(PropertyService propertyService, BookingService bookingService) {
        this.propertyService = propertyService;
        this.bookingService = bookingService;
    }

    public void run(Scanner scanner, Homeowner owner) {
        while (true) {
            System.out.println("=== Homeowner Menu ===");
            System.out.println("Logged in as: " + owner.getName() + " (" + owner.getRole() + ")");
            System.out.println("1) List a property");
            System.out.println("2) Add a room to a property");
            System.out.println("3) View my properties");
            System.out.println("4) Update a property");
            System.out.println("5) Remove a property");
            System.out.println("6) Update a room");
            System.out.println("7) Remove a room");
            System.out.println("8) View pending booking requests");
            System.out.println("9) Respond to booking request (ACCEPT/REJECT)");
            System.out.println("10) View all my bookings");
            System.out.println("0) Logout");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> listProperty(scanner, owner);
                    case "2" -> addRoom(scanner, owner);
                    case "3" -> viewMyProperties(owner);
                    case "4" -> updateProperty(scanner, owner);
                    case "5" -> removeProperty(scanner, owner);
                    case "6" -> updateRoom(scanner, owner);
                    case "7" -> removeRoom(scanner, owner);
                    case "8" -> viewPendingRequests(owner);
                    case "9" -> respondToRequest(scanner, owner);
                    case "10" -> viewAllBookings(owner);
                    case "0" -> { System.out.println("Logged out."); return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            }

            System.out.println();
        }
    }

    private void listProperty(Scanner scanner, Homeowner owner) {
        System.out.print("Address: ");
        String address = scanner.nextLine();

        System.out.print("City: ");
        String city = scanner.nextLine();

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        var property = propertyService.createProperty(owner.getUserId(), address, city, desc);
        System.out.println("Created property: " + property.getPropertyId());
    }

    private void addRoom(Scanner scanner, Homeowner owner) {
        System.out.print("Property ID: ");
        String propertyId = scanner.nextLine().trim();

        RoomType type = readRoomType(scanner);

        System.out.print("Monthly rent (e.g. 550): ");
        double rent = Double.parseDouble(scanner.nextLine().trim());

        List<String> amenities = readAmenities(scanner);

        LocalDate from = readDate(scanner, "Available from (YYYY-MM-DD): ");
        LocalDate to = readDate(scanner, "Available to (YYYY-MM-DD): ");

        var room = propertyService.addRoomToProperty(owner.getUserId(), propertyId, type, rent, amenities, from, to);
        System.out.println("Added room: " + room.getRoomId());
    }

    private void viewMyProperties(Homeowner owner) {
        var properties = propertyService.viewOwnerProperties(owner.getUserId());
        if (properties.isEmpty()) {
            System.out.println("No properties yet.");
            return;
        }

        for (var p : properties) {
            System.out.println(p + " | desc=" + p.getDescription());
            for (var r : p.getRooms()) {
                System.out.println("  - " + r);
            }
        }
    }

    private void updateProperty(Scanner scanner, Homeowner owner) {
        System.out.print("Property ID: ");
        String propertyId = scanner.nextLine().trim();

        System.out.println("Leave blank to keep current value.");
        System.out.print("New address: ");
        String newAddress = scanner.nextLine();

        System.out.print("New city (only allowed if property has no rooms): ");
        String newCity = scanner.nextLine();

        System.out.print("New description: ");
        String newDesc = scanner.nextLine();

        var updated = propertyService.updateProperty(owner.getUserId(), propertyId,
                blankToNull(newAddress), blankToNull(newCity), blankToNull(newDesc));

        System.out.println("Updated: " + updated);
    }

    private void removeProperty(Scanner scanner, Homeowner owner) {
        System.out.print("Property ID to remove: ");
        String propertyId = scanner.nextLine().trim();

        propertyService.removeProperty(owner.getUserId(), propertyId);
        System.out.println("Property removed (and its rooms removed from search index).");
    }

    private void updateRoom(Scanner scanner, Homeowner owner) {
        System.out.print("Property ID: ");
        String propertyId = scanner.nextLine().trim();

        System.out.print("Room ID: ");
        String roomId = scanner.nextLine().trim();

        System.out.println("Leave blank to keep current value.");

        RoomType newType = readOptionalRoomType(scanner);

        Double newRent = readOptionalDouble(scanner, "New monthly rent: ");

        List<String> newAmenities = readOptionalAmenities(scanner);

        LocalDate newFrom = readOptionalDate(scanner, "New available from (YYYY-MM-DD): ");
        LocalDate newTo = readOptionalDate(scanner, "New available to (YYYY-MM-DD): ");

        var updated = propertyService.updateRoom(owner.getUserId(), propertyId, roomId,
                newType, newRent, newAmenities, newFrom, newTo);

        System.out.println("Updated room: " + updated);
    }

    private void removeRoom(Scanner scanner, Homeowner owner) {
        System.out.print("Property ID: ");
        String propertyId = scanner.nextLine().trim();

        System.out.print("Room ID to remove: ");
        String roomId = scanner.nextLine().trim();

        propertyService.removeRoom(owner.getUserId(), propertyId, roomId);
        System.out.println("Room removed.");
    }

    private void viewPendingRequests(Homeowner owner) {
        var pending = bookingService.getPendingRequestsForOwner(owner.getUserId());
        if (pending.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }
        System.out.println("Pending requests:");
        for (var b : pending) {
            System.out.println(" - " + b.getBookingId()
                    + " | room=" + b.getRoomId()
                    + " | student=" + b.getStudentId()
                    + " | " + b.getStartDate() + " to " + b.getEndDate()
                    + " | status=" + b.getStatus());
        }
    }

    private void respondToRequest(Scanner scanner, Homeowner owner) {
        System.out.print("Booking ID: ");
        String bookingId = scanner.nextLine().trim();

        BookingStatus decision = readDecision(scanner);

        var updated = bookingService.respondToRequest(owner.getUserId(), bookingId, decision);
        System.out.println("Updated: " + updated.getBookingId() + " status=" + updated.getStatus());
    }

    private void viewAllBookings(Homeowner owner) {
        var bookings = bookingService.getBookingsForOwner(owner.getUserId());
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }
        System.out.println("All my bookings:");
        for (var b : bookings) {
            System.out.println(" - " + b);
        }
    }

    // ---------- helpers ----------

    private BookingStatus readDecision(Scanner scanner) {
        while (true) {
            System.out.print("Decision (1=ACCEPT, 2=REJECT): ");
            String s = scanner.nextLine().trim();
            if ("1".equals(s)) return BookingStatus.ACCEPTED;
            if ("2".equals(s)) return BookingStatus.REJECTED;
            System.out.println("Invalid choice.");
        }
    }

    private RoomType readRoomType(Scanner scanner) {
        while (true) {
            System.out.print("Room type (1=SINGLE, 2=DOUBLE): ");
            String c = scanner.nextLine().trim();
            if ("1".equals(c)) return RoomType.SINGLE;
            if ("2".equals(c)) return RoomType.DOUBLE;
            System.out.println("Invalid choice.");
        }
    }

    private RoomType readOptionalRoomType(Scanner scanner) {
        while (true) {
            System.out.print("New room type (Enter=keep, 1=SINGLE, 2=DOUBLE): ");
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return null;
            if ("1".equals(s)) return RoomType.SINGLE;
            if ("2".equals(s)) return RoomType.DOUBLE;
            System.out.println("Invalid choice.");
        }
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

    private List<String> readAmenities(Scanner scanner) {
        System.out.print("Amenities (comma-separated, e.g. WiFi,Desk,Ensuite): ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return List.of();

        String[] parts = line.split(",");
        List<String> amenities = new ArrayList<>();
        for (String p : parts) {
            String s = p.trim();
            if (!s.isEmpty()) amenities.add(s);
        }
        return amenities;
    }

    private List<String> readOptionalAmenities(Scanner scanner) {
        System.out.print("New amenities (Enter=keep, comma-separated): ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return null;
        String[] parts = line.split(",");
        List<String> amenities = new ArrayList<>();
        for (String p : parts) {
            String s = p.trim();
            if (!s.isEmpty()) amenities.add(s);
        }
        return amenities;
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

    private String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
