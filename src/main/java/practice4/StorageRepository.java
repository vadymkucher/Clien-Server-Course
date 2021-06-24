package practice4;

import java.sql.*;
import java.util.List;

public class StorageRepository {

    private static final String DB_URL = "jdbc:sqlite:C:/sqlite/storage.db";

    private Connection connection;
    private GroupRepository groupRepository;

    public void connect() {
        connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL);
            groupRepository = new GroupRepository(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet findAllGroupsOfProduct(long productId) {
        PreparedStatement statement;
        try {
            String query = "SELECT * from product_groups where product_id = ?";
            statement = connection.prepareStatement(query);
            statement.setLong(1, productId);
            return statement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateProductGroups(String productName, List<String> newGroups) {

        String query = "DELETE FROM product_groups WHERE product_id=?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, getProductId(productName));
            statement.executeUpdate();
            addGroupsToProduct(getProductId(productName), newGroups);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void addGroupsToProduct(int productId, List<String> groups) {
        for (String group : groups) {
            groupRepository.checkGroup(group);
            String query = "INSERT INTO product_groups(product_id, group_id) VALUES(?,?)";
            try {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, productId);
                statement.setInt(2, groupRepository.findGroup(group).getInt("group_id"));
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    public ResultSet findByGroup(String groupName) {
        PreparedStatement statement;
        try {
            String query = "SELECT * from storage where product_id IN (SELECT product_id from product_groups p join groups g on p.group_id = g.group_id where g.group_name = ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, groupName);
            return statement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int getProductId(String productName) throws SQLException {
        ResultSet productDB = findByProductName(productName);
        return productDB.getInt("product_id");
    }


    public void save(Product newProduct) {
        String query = "INSERT INTO storage(product_name, amount, price) VALUES(?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newProduct.getName());
            groupRepository.saveGroups(newProduct.getGroups());
            statement.setInt(2, newProduct.getAmount());
            statement.setDouble(3, newProduct.getPrice());
            statement.executeUpdate();

            addGroupsToProduct(getProductId(newProduct.getName()), newProduct.getGroups());

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void save(List<Product> productList) {
        for (Product product : productList) {
            save(product);
        }
    }


    public void update(String productName, String newProductName, int amount, double price) {
        try {
            String query = "UPDATE storage SET product_name=?, amount=?, price=? WHERE product_name=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newProductName);
            statement.setInt(2, amount);
            statement.setDouble(3, price);
            statement.setString(4, productName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(long id, Product product) {
        try {
            String query = "UPDATE storage SET product_name=?, amount=?, price=? WHERE product_id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, product.getName());
            statement.setInt(2, product.getAmount());
            statement.setDouble(3, product.getPrice());
            statement.setLong(4, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ResultSet findByProductName(String productName) {
        PreparedStatement statement;
        try {
            String query = "SELECT * FROM storage WHERE product_name=?";
            statement = connection.prepareStatement(query);
            statement.setString(1, productName);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void delete(String productName) {
        try {
            String query = "DELETE from storage WHERE product_name=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, productName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet listByAmount(int minAmount, int maxAmount) {
        String query = "SELECT * from storage WHERE amount BETWEEN ? AND ?";

        try {
            return listByParameter(minAmount, maxAmount, query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ResultSet listByPrice(double minPrice, double maxPrice) {
        String query = "SELECT * from storage WHERE price BETWEEN ? AND ?";

        try {
            return listByParameter(minPrice, maxPrice, query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ResultSet listByParameter(double min, double max, String query) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setDouble(1, min);
        statement.setDouble(2, max);
        return statement.executeQuery();
    }


    public Connection getConnection() {
        return connection;
    }


    public GroupRepository getGroupRepository() {
        return groupRepository;
    }

    public ResultSet findProductById(long id) {
        PreparedStatement statement;
        try {
            String query = "SELECT * FROM storage WHERE product_id=?";
            statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean validateParameters(Product product) {
        if (product.getName().equals("") || product.getName() == null) {
            return false;
        }
        if (product.getAmount() < 0) {
            return false;
        }
        if (product.getPrice() < 0) {
            return false;
        }
        return true;
    }
}



