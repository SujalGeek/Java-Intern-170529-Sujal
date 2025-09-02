import java.sql.*;
import java.util.Scanner;

public class PracticeJDBC {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/lenden";
    private static final String username = "root";
    private static final String password = "Waheguru\"071!";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            // Debit from sender
            String debit_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";

            // Credit to receiver (fix: balance + ? not balance = ?)
            String credit_query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

            PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);
            PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);

            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter debit account number: ");
            int debitAccount = scanner.nextInt();

            System.out.print("Enter credit account number: ");
            int creditAccount = scanner.nextInt();

            System.out.print("Enter amount: ");
            double amount = scanner.nextDouble();

            // Check sufficient balance before transaction
            if (isSufficient(connection, debitAccount, amount)) {
                connection.setAutoCommit(false); // transaction start

                debitPreparedStatement.setDouble(1, amount);
                debitPreparedStatement.setInt(2, debitAccount);

                creditPreparedStatement.setDouble(1, amount);
                creditPreparedStatement.setInt(2, creditAccount);

                int affectedRows1 = debitPreparedStatement.executeUpdate();
                int affectedRows2 = creditPreparedStatement.executeUpdate();

                if (affectedRows1 > 0 && affectedRows2 > 0) {
                    connection.commit(); // transaction success
                    System.out.println("Transaction successful!");
                } else {
                    connection.rollback(); // transaction failed
                    System.out.println("Transaction failed, rolled back.");
                }

                connection.setAutoCommit(true);
            } else {
                System.out.println("Insufficient balance");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static boolean isSufficient(Connection connection, int account_number, double amount) {
        try {
            String query = "SELECT balance FROM accounts WHERE account_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double current_balance = resultSet.getDouble("balance");
                return amount <= current_balance;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false; // fix: ensure method always returns a value
    }
}
