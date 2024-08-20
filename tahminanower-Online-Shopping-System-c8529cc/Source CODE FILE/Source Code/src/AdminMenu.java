import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Date;

public class AdminMenu {

    private static Scanner scanner = new Scanner(System.in);

    public static void displayMenu() {
        while (true) {
            System.out.println("====================================================");
            System.out.println();
            System.out.println("\nAdmin Menu");
            System.out.println("1. Add New Products to the Inventory");
            System.out.println("2. Update Product's Price/Stock");
            System.out.println("3. Delete Product");
            System.out.println("4. Sales Report");
            System.out.println("5. Customer Order History");
            System.out.println("6. Logout");
            System.out.println();
            System.out.println("====================================================");
            System.out.print("Enter your choice(1 to 6) ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    updateProduct();
                    break;
                case 3:
                    deleteProduct();
                    break;
                case 4:
                    checkAllDetails();
                    break;
                case 5:
                    customerHistory();
                    break;
                case 6:
                    return; // Logout
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addProduct() {
        System.out.print("Enter Product Name: ");
        String name = scanner.next();
        System.out.print("Enter Price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        System.out.print("Enter Category: ");  //ADD
        String name1 = scanner.next();
        System.out.print("Enter Description: ");  //ADD
        String des = scanner.next();

        String sql = "INSERT INTO Products (Name, Price, Quantity, Category, Description) VALUES (?, ?, ?, ?, ?)"; //ADD
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setDouble(2, price);
            pst.setInt(3, quantity);
            pst.setString(4, name1); //ADD
            pst.setString(5, des); //ADD
            int result = pst.executeUpdate();
            if (result > 0) {
                System.out.println("\nProduct added successfully.\n");
            } else {
                System.out.println("\nError adding product.\n");
            }
        } catch (SQLException e) {
            System.out.println("Error while adding product: " + e.getMessage());
        }
    }



    private static void updateProduct() {
        System.out.print("Enter Product ID to Update: ");
        int productId = scanner.nextInt();
        System.out.print("Enter New Price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter New Quantity: ");
        int quantity = scanner.nextInt();

        String sql = "UPDATE Products SET Price = ?, Quantity = ? WHERE ProductID = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDouble(1, price);
            pst.setInt(2, quantity);
            pst.setInt(3, productId);
            int result = pst.executeUpdate();
            if (result > 0) {
                System.out.println("\nProduct updated successfully.\n");
            } else {
                System.out.println("\nError updating product.\n");
            }
        } catch (SQLException e) {
            System.out.println("Error while updating product: " + e.getMessage());
        }
    }

    private static void deleteProduct() {
        System.out.print("Enter Product ID to Delete: ");
        int productId = scanner.nextInt();

        String sql = "DELETE FROM Products WHERE ProductID = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, productId);
            int result = pst.executeUpdate();
            if (result > 0) {
                System.out.println("\nProduct deleted successfully.\n");
            } else {
                System.out.println("\nError deleting product.\n");
            }
        } catch (SQLException e) {
            System.out.println("Error while deleting product: " + e.getMessage());
        }
    }

    private static void checkAllDetails() {
        String sql = "SELECT p.ProductID, p.Name, p.Price, p.Quantity, COALESCE(SUM(o.Quantity), 0) AS TotalSold " +
                "FROM Products p LEFT JOIN Orders o ON p.ProductID = o.ProductID " +
                "GROUP BY p.ProductID, p.Name, p.Price, p.Quantity " +
                "ORDER BY p.ProductID";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            System.out.println("\nProduct Details and Sales Information:");
            System.out.println();
            while (rs.next()) {
                int id = rs.getInt("ProductID");
                String name = rs.getString("Name");
                double price = rs.getDouble("Price");
                int quantity = rs.getInt("Quantity");
                int totalSold = rs.getInt("TotalSold");

                System.out.println("Product ID: "+ id + "   Name: "+ name + "   Price: " + price + "   Quantity: "+ quantity + "   Total Sold: "+ totalSold);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving product details: " + e.getMessage());
        }
    }

    private static void customerHistory() {
        String sql = "SELECT ORDERID, USERID, PRODUCTID, QUANTITY, ORDERDATE FROM ORDERS";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int orderId = rs.getInt("ORDERID");
                int userId = rs.getInt("USERID");
                int productId = rs.getInt("PRODUCTID");
                int quantity = rs.getInt("QUANTITY");
                Date orderDate = rs.getDate("ORDERDATE");

                System.out.println();
                System.out.println("Order ID: " + orderId + ", User ID: " + userId + ", Product ID: " + productId
                        + ", Quantity: " + quantity + ", Order Date: " + orderDate);
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println("Error while retrieving orders: " + e.getMessage());
        }
    }

}