import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomerMenu {

    private static Scanner scanner = new Scanner(System.in);
    private static List<Integer> cart = new ArrayList<>();
    private static int currentUserId = 1;
    public static void displayMenu() {
        while (true) {
            System.out.println();
            System.out.println("====================================================");
            System.out.println("Customer Menu");
            System.out.println();
            System.out.println("1. View All Products");
            System.out.println("2. Search Product by Name");
            System.out.println("3. Search Product by Category");
            System.out.println("4. Add to Cart");
            System.out.println("5. Checkout");
            System.out.println("6. Logout");
            System.out.println("====================================================");
            System.out.println();
            System.out.print("Enter your choice(1 to 6): ");

            int choice;
            while (true) {
                try {
                    choice = scanner.nextInt();
                    break; // Break the loop if input is valid
                } catch (java.util.InputMismatchException e) {
                    // Handle non-integer input
                    System.out.println("Invalid input. Please enter a number (1 to 6): ");
                    scanner.nextLine(); // Clear the input buffer
                }
            }

            switch (choice) {
                case 1:
                    System.out.println("\nTHE ITEMS FOR SALE TODAY\n");
                    viewAllProducts();
                    break;
                case 2:
                    searchProduct();
                    break;
                case 3:
                    searchCategory();
                    break;
                case 4:
                    buyProduct();
                    break;
                case 5:
                    checkout();
                    break;
                case 6:
                    System.out.println("\nYou have been logged out. Please come back again!\n");
                    return; // Exit
                default:
                    System.out.println("\nInvalid option. Please try again./n");

            }
        }
    }




    private static void viewAllProducts() {
        String sql = "SELECT PRODUCTID, NAME, PRICE, QUANTITY, CATEGORY, DESCRIPTION FROM Products";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                int productId = rs.getInt("PRODUCTID");
                String productName = rs.getString("NAME");
                double productPrice = rs.getDouble("PRICE");
                int productQuantity = rs.getInt("QUANTITY");
                String cat = rs.getString("CATEGORY");  //ADD
                String des = rs.getString("DESCRIPTION"); //ADD
                System.out.println();
                System.out.println("Product ID: " + productId + "   Name: " + productName + "   Quantity: " + productQuantity + "   Category: " + cat + "   Price: " + productPrice + "   Description: " + des);

            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println("Error while retrieving products: " + e.getMessage());
        }
    }

    private static void searchProduct() {
        System.out.print("Enter Product Name: ");
        String searchTerm = scanner.next();
        String sql = "SELECT ProductID, Name, Price, Quantity , Description FROM Products WHERE Name LIKE ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, "%" + searchTerm + "%");
//            pst.setString(2, searchTerm); // Assuming ID is also passed as a string
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("ProductID") + ", Name: " + rs.getString("Name")
                            + ", Price: " + rs.getDouble("Price") + ", Quantity: " + rs.getInt("Quantity") + ", Description: " + rs.getString("Description")); //add
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while searching for product: " + e.getMessage());
        }
    }


    private static void searchCategory() {  //ADD
        String sql ="SELECT DISTINCT CATEGORY FROM Products";
        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                System.out.println("Category: " + rs.getString("Category"));
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving products: " + e.getMessage());
        }
        System.out.print("Enter Category: ");
        String searchTerm = scanner.next();
        String sql1= "SELECT ProductID, Name, Price, Quantity,Description FROM Products WHERE Category LIKE ?"; //add
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql1)){
            pst.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("ProductID") + ", Name: " + rs.getString("Name")
                            + ", Price: " + rs.getDouble("Price") + ", Quantity: " + rs.getInt("Quantity") + ", Description: " + rs.getString("Description")); //add
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while searching for product: " + e.getMessage());
        }
    }
    //ADD

    private static void buyProduct() {
        System.out.print("Enter Product ID to add to cart: ");
        int productId = scanner.nextInt();
        cart.add(productId); // For simplicity, we are adding directly to the cart.
        System.out.println();
        System.out.println("Product added to cart.");
    }

    private static void checkout() {
        if (cart.isEmpty()) {
            System.out.println();
            System.out.println("Your cart is empty.");
            System.out.println();
            return;
        }

        double total = 0;
        System.out.println();
        System.out.println("Products in your cart:");
        System.out.println();
        for (int productId : cart) {
            ProductDetails product = getProductDetails(productId);
            if (product != null) {
                System.out.println("ID: " + product.getId() + ", Name: " + product.getName() + ", Price: " + product.getPrice());
                total += product.getPrice();
            }
        }
        System.out.println();
        System.out.println("Total: " + total);
        System.out.print("\nDo you want to complete the purchase? (yes/no): ");
        String confirmation = scanner.next();
        if ("yes".equalsIgnoreCase(confirmation)) {
            completePurchase();
            System.out.println("\nPurchase completed successfully.\n");
            cart.clear();
        } else {
            System.out.println("\nPurchase cancelled.\n");
        }
    }

    private static ProductDetails getProductDetails(int productId) {
        String sql = "SELECT ProductID, Name, Price FROM Products WHERE ProductID = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, productId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new ProductDetails(
                            rs.getInt("ProductID"),
                            rs.getString("Name"),
                            rs.getDouble("Price")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving product details: " + e.getMessage());
        }
        return null;
    }

    private static void completePurchase() {
        if (cart.isEmpty()) {
            System.out.println("\nThe cart is empty. No purchase to complete.\n");
            return;
        }

        String orderSql = "INSERT INTO Orders (UserID, ProductID, Quantity, OrderDate) VALUES (?, ?, ?, CURRENT_DATE)";
        String updateProductSql = "UPDATE Products SET Quantity = Quantity - ? WHERE ProductID = ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement orderPst = con.prepareStatement(orderSql);
                 PreparedStatement updateProductPst = con.prepareStatement(updateProductSql)) {

                for (int productId : cart) {
                    int userId = getCurrentUserId();

                    // Update Orders table
                    orderPst.setInt(1, userId);
                    orderPst.setInt(2, productId);
                    orderPst.setInt(3, 1);
                    orderPst.executeUpdate();

                    // Update Products table
                    updateProductPst.setInt(1, 1);
                    updateProductPst.setInt(2, productId);
                    updateProductPst.executeUpdate();
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                System.out.println("Error during purchase transaction: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        cart.clear();
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    private static int getCurrentUserId() {
        return currentUserId;
    }


    private static class ProductDetails {
        private int id;
        private String name;
        private double price;

        public ProductDetails(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }


}