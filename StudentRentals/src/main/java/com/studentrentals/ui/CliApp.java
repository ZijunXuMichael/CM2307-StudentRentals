package com.studentrentals.ui;

import com.studentrentals.repository.BookingRepository;
import com.studentrentals.repository.PropertyRepository;
import com.studentrentals.repository.RoomRepository;
import com.studentrentals.repository.UserRepository;
import com.studentrentals.service.AuthService;
import com.studentrentals.service.BookingService;
import com.studentrentals.service.PropertyService;

import java.util.Scanner;

public class CliApp {

    // Shared repositories
    private final UserRepository userRepository = new UserRepository();
    private final PropertyRepository propertyRepository = new PropertyRepository();
    private final RoomRepository roomRepository = new RoomRepository();
    private final BookingRepository bookingRepository = new BookingRepository();

    // Shared services
    private final AuthService authService = new AuthService(userRepository);
    private final PropertyService propertyService = new PropertyService(propertyRepository, roomRepository);
    private final BookingService bookingService = new BookingService(roomRepository, bookingRepository);

    // Menus (DI)
    private final StudentMenu studentMenu = new StudentMenu(roomRepository, bookingService);
    private final HomeownerMenu homeownerMenu = new HomeownerMenu(propertyService, bookingService);

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                printMainMenu();
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> registerStudent(scanner);
                    case "2" -> registerHomeowner(scanner);
                    case "3" -> loginAndRoute(scanner);
                    case "0" -> {
                        System.out.println("Bye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }

                System.out.println();
            }
        }
    }

    private void printMainMenu() {
        System.out.println("=== StudentRentals CLI ===");
        System.out.println("1) Register Student");
        System.out.println("2) Register Homeowner");
        System.out.println("3) Login");
        System.out.println("0) Exit");
        System.out.print("Choose: ");
    }

    private void registerStudent(Scanner scanner) {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Password: ");
            String pw = scanner.nextLine();

            System.out.print("University: ");
            String uni = scanner.nextLine();

            System.out.print("Student ID: ");
            String sid = scanner.nextLine();

            authService.registerStudent(name, email, pw, uni, sid);
            System.out.println("Registered successfully (Student).");
        } catch (IllegalArgumentException ex) {
            System.out.println("Registration failed: " + ex.getMessage());
        }
    }

    private void registerHomeowner(Scanner scanner) {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Password: ");
            String pw = scanner.nextLine();

            System.out.print("Contact number: ");
            String contact = scanner.nextLine();

            authService.registerHomeowner(name, email, pw, contact);
            System.out.println("Registered successfully (Homeowner).");
        } catch (IllegalArgumentException ex) {
            System.out.println("Registration failed: " + ex.getMessage());
        }
    }

    private void loginAndRoute(Scanner scanner) {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Password: ");
            String pw = scanner.nextLine();

            var user = authService.authenticate(email, pw);
            System.out.println("Login successful.");

            if (user instanceof com.studentrentals.model.user.Student s) {
                studentMenu.run(scanner, s);
            } else if (user instanceof com.studentrentals.model.user.Homeowner h) {
                homeownerMenu.run(scanner, h);
            } else {
                System.out.println("Unknown user type.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Login failed: " + ex.getMessage());
        }
    }
}
