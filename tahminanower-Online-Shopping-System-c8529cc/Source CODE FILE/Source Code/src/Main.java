import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            displayMainMenu();

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleAccountCreation();
                    break;
                case 3:
                    contactUs();
                    break;
                case 4:
                    exitApplication();
                    return;
                default:
                    System.out.println();
                    System.out.println("Invalid option. Please try again.");
                    System.out.println();
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("====================================================");
        System.out.println();
        System.out.println("WELCOME TO THE NIKO ONLINE SHOE STORE");
        System.out.println();
        System.out.println("1. Login");
        System.out.println("2. Create Account");
        System.out.println("3. Contact Us");
        System.out.println("4. Exit");
        System.out.println();
        System.out.println("====================================================");
        System.out.println();
        System.out.print("Enter your choice(1 to 4): ");

    }

    private static void handleLogin() {

        System.out.print("Enter Username: ");
        String username = scanner.next().trim();
        System.out.print("Enter Password: ");
        String password = scanner.next().trim();


        System.out.println("\nSigning into your account. Please wait...");

        String sql = "SELECT Role FROM Users WHERE UPPER(Username) = UPPER(?) AND Password = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("Role");
                    if ("admin".equalsIgnoreCase(role)) {
                        AdminMenu.displayMenu();
                    } else if ("customer".equalsIgnoreCase(role)) {
                        CustomerMenu.displayMenu();
                    }
                } else {
                    System.out.println("\nInvalid username or password. Please try again.\n");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while trying to login: " + e.getMessage());
        }
    }


    private static void handleAccountCreation() {
        System.out.println("====================================================");
        System.out.println();
        System.out.println("\nAccount Type:");
        System.out.println("1. Admin");
        System.out.println("2. Customer");
        System.out.println();
        System.out.println("====================================================");
        System.out.println();
        System.out.print("Choose account type (1 for Admin, 2 for Customer): ");
        int accountTypeChoice = scanner.nextInt();
        scanner.nextLine();

        String role;
        String fullName = "";
        String email = "";
        String address = "";

        if (accountTypeChoice == 1) {
            role = "admin";
        } else if (accountTypeChoice == 2) {
            role = "customer";
            System.out.print("Enter Full Name: ");
            fullName = scanner.nextLine();

            System.out.print("Enter Email: ");
            email = scanner.nextLine();

            System.out.print("Enter Address: ");
            address = scanner.nextLine();
        } else {
            System.out.println("Invalid choice. Please try again.");
            return;
        }

        System.out.print("Enter Username: ");
        String newUsername = scanner.next();
        System.out.print("\nVerifying Username. Please wait...");

        if (usernameExists(newUsername)) {
            System.out.println("\nThis username already exists. Please try a different username.\n");
            return;
        }
        System.out.println();
        System.out.print("\nUserName Approved\n");
        System.out.println();

        System.out.print("Enter Password: ");
        String newPassword = scanner.next();
        System.out.println();

        String sql = "INSERT INTO Users (Username, Password, Role, FULLNAME, EMAIL, ADDRESS) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, newUsername);
            pst.setString(2, newPassword);
            pst.setString(3, role);
            pst.setString(4, fullName);
            pst.setString(5, email);
            pst.setString(6, address);
            int result = pst.executeUpdate();
            if (result > 0) {
                System.out.println(role + " account created successfully.");
            } else {
                System.out.println("Error creating account.");
            }
        } catch (SQLException e) {
            System.out.println("Error while trying to create account: " + e.getMessage());
        }
    }

    private static boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while checking username existence: " + e.getMessage());
        }
        return false;
    }


    private static void exitApplication() {
        System.out.println();
        System.out.println("====================================================");
        System.out.println();
        System.out.println("Hope You Enjoyed Shopping With Us.\n\nHave A Wonderful Day\n\nPlease Come Back again!");
        System.out.println();
        System.out.println("Closing Niko Online Shoe Store");
        System.out.println();
        System.out.println("====================================================");
        System.out.println();
    }

    private static void contactUs(){
        System.out.println();
        System.out.println("Contact Us For Support");
        System.out.println();
        System.out.println("1) Tahmin Anower (Niko CEO) Phone: 55154870");
        System.out.println();
        System.out.println("2) Lubaba Hassan (Niko President) Phone: 9801515");
        System.out.println();
        System.out.println("3) Diana Kim (Niko Director General) Phone: 778542145");
        System.out.println();
        System.out.println("4) Pranav Keda (Niko COO) Phone: 144521124");
        System.out.println();
        System.out.println("====================================================");
        return;


    }
}