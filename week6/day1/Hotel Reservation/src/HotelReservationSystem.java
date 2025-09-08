import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "Waheguru\"071!";

    public static void main(String[] args) throws SQLException, ClassNotFoundException , Exception{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println();
                System.out.println("Hotel Management System");
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get a Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1 -> reserveRoom(connection, sc);
                    case 2 -> viewReservation(connection);
                    case 3 -> getRoomNumber(connection, sc);
                    case 4 -> updateReservation(connection, sc);
                    case 5 -> deleteReservation(connection, sc);
                    case 0 -> {
                        exit();
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Invalid Choice! Try again!! ");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Reserve Room
    private static void reserveRoom(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter the guest name: ");
            sc.nextLine(); // consume newline
            String guestName = sc.nextLine();

            System.out.println("Enter the room number: ");
            int roomNumber = sc.nextInt();

            System.out.println("Enter the contact number: ");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservation(guest_name, room_number, contact_number) VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, guestName);
                ps.setInt(2, roomNumber);
                ps.setString(3, contactNumber);

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Reservation Successful!!");
                } else {
                    System.out.println("Reservation Failed");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // View Reservations
    private static void viewReservation(Connection connection) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservation";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Current Reservations.");
            System.out.println("--------------------------------------------------------------------------------------------");
            System.out.println("| Reservation ID | Guest           | Room Number | Contact Number     | Reservation Date   |");
            System.out.println("--------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getString("reservation_date");

                System.out.printf("| %14d | %-15s | %-11d | %-18s | %-18s |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }
            System.out.println("--------------------------------------------------------------------------------------------");
        }
    }

    // Get Room Number
    private static void getRoomNumber(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter the reservation ID: ");
            int reservationId = sc.nextInt();

            System.out.println("Enter the guest name: ");
            String guestName = sc.next();

            String sql = "SELECT room_number FROM reservation WHERE reservation_id = ? AND guest_name = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, reservationId);
                ps.setString(2, guestName);

                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        int roomNumber = resultSet.getInt("room_number");
                        System.out.println("Room number for ReservationId " + reservationId +
                                " and Guest " + guestName + " is: " + roomNumber);
                    } else {
                        System.out.println("Reservation not found for the given ID and guest name.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Update Reservation
    private static void updateReservation(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter the reservation ID: ");
            int reservationId = sc.nextInt();
            sc.nextLine(); // consume newline

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID");
                return;
            }

            System.out.println("Enter new guest Name: ");
            String newGuestName = sc.nextLine();

            System.out.println("Enter the room Number: ");
            int newRoomNumber = sc.nextInt();

            System.out.println("Enter the new Contact Number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservation SET guest_name = ?, room_number = ?, contact_number = ? WHERE reservation_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, newGuestName);
                ps.setInt(2, newRoomNumber);
                ps.setString(3, newContactNumber);
                ps.setInt(4, reservationId);

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Reservation Updated Successfully!");
                } else {
                    System.out.println("Reservation update failed");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Delete Reservation
    private static void deleteReservation(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter the reservation ID: ");
            int reservationId = sc.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, reservationId);

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted Successfully!!");
                } else {
                    System.out.println("Reservation Deletion failed!!");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Check if Reservation Exists
    private static boolean reservationExists(Connection connection, int reservationId) {
        String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Exit
    public static void exit() throws InterruptedException {
        System.out.println("Exiting System");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank You for Using Hotel Reservation System!!!");
    }
}
