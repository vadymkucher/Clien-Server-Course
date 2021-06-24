package practice4;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class StorageRepositoryTest {
    private static StorageRepository storageRepository;
    private static Connection dbConnection;

    @BeforeAll
    static void setConnection() {
        storageRepository = new StorageRepository();
        storageRepository.connect();
        dbConnection = storageRepository.getConnection();


        editTestTable("DROP TABLE storage");
        editTestTable("CREATE TABLE storage (\n" +
                "  product_id   integer PRIMARY KEY,\n" +
                "  product_name text    NOT NULL UNIQUE,\n" +
                "  amount       integer NOT NULL,\n" +
                "  price        real    NOT NULL\n" +
                ")");

        editTestTable("DROP TABLE groups");
        editTestTable("CREATE TABLE groups (\n" +
                "  group_id   integer PRIMARY KEY,\n" +
                "  group_name text NOT NULL UNIQUE\n" +
                ")");

        editTestTable("DROP TABLE product_groups");
        editTestTable("CREATE TABLE product_groups (\n" +
                "  product_id   integer NOT NULL ,\n" +
                "  group_id   integer NOT NULL\n" +
                ")");
    }

    private static void editTestTable(String statement) {
        Statement dropStatement;
        try {
            dropStatement = dbConnection.createStatement();
            dropStatement.executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int calculateTableSize() throws SQLException {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = dbConnection.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM storage");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet.getInt(1);
    }

    @Test
    public void addOneProduct() throws SQLException {
        storageRepository.save(new Product("test1", new LinkedList<>(Collections.singletonList("food1")), 340, 12.70));

        String query = "SELECT * FROM storage WHERE product_name=?";
        PreparedStatement statement;
        ResultSet resultSet = null;
        try {
            statement = dbConnection.prepareStatement(query);
            statement.setString(1, "test1");
            resultSet = statement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals("test1", resultSet.getString("product_name"));
    }

    @Test
    public void addListOfProducts() throws SQLException {

        int initialDBSize = calculateTableSize();

        List<Product> productList = new LinkedList<>(Arrays.asList(
                new Product("test21", new LinkedList<>(Collections.singletonList("food1")), 340, 12.70),
                new Product("test22", new LinkedList<>(Collections.singletonList("food1")), 220, 12.70),
                new Product("test33", new LinkedList<>(Collections.singletonList("food1")), 10, 17.00)));

        storageRepository.save(productList);
        int newDBSize = calculateTableSize();

        Assertions.assertEquals(initialDBSize + productList.size(), newDBSize);
    }

    @Test
    public void updateProduct() throws SQLException {

        String initialGroup = "food";
        int initialAmount = 200;
        double initialPrice = 10;
        storageRepository.save(new Product("papaaaaaaaaaya", new LinkedList<>(Collections.singletonList(initialGroup)), initialAmount, initialPrice));
        int newAmount = 999;
        double newPrice = 20;

        storageRepository.update("papaaaaaaaaaya", "papaaaaaaaaaya", newAmount, newPrice);

        String query = "SELECT * FROM storage WHERE product_name=?";
        PreparedStatement statement;
        ResultSet resultSet = null;
        try {
            statement = dbConnection.prepareStatement(query);
            statement.setString(1, "papaaaaaaaaaya");
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals("papaaaaaaaaaya", resultSet.getString("product_name"));
        Assertions.assertEquals(newAmount, resultSet.getInt("amount"));
        Assertions.assertEquals(newPrice, resultSet.getDouble("price"));
    }


    @Test
    public void updateGroup() throws SQLException {

        storageRepository.updateProductGroups("papaaaaaaaaaya", Collections.singletonList("god food"));

        ResultSet group = storageRepository.getGroupRepository().findGroup("god food");
        int productId = storageRepository.getProductId("papaaaaaaaaaya");

        String query = "SELECT group_id FROM product_groups WHERE product_id=?";
        PreparedStatement statement;
        ResultSet resultSet = null;
        try {
            statement = dbConnection.prepareStatement(query);
            statement.setInt(1, productId);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        int groupId = group.getInt("group_id");
        Assertions.assertEquals(groupId, resultSet.getInt("group_id"));

    }

    @Test
    public void updateProductName() throws SQLException {

        String group = "test3";
        int amount = 5;
        double price = 15000;
        storageRepository.save(new Product("test31", new LinkedList<>(Collections.singletonList(group)), amount, price));
        storageRepository.update("test31", "test32", amount, price);

        String query = "SELECT * FROM storage WHERE product_name=?";
        PreparedStatement statement;
        ResultSet resultSet = null;
        try {
            statement = dbConnection.prepareStatement(query);
            statement.setString(1, "test32");
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals("test32", resultSet.getString("product_name"));
        Assertions.assertEquals(amount, resultSet.getInt("amount"));
        Assertions.assertEquals(price, resultSet.getDouble("price"));
    }

    @Test
    public void findProductByName() {
        ResultSet resultSet = storageRepository.findByProductName("aaaaa");
        try {
            Assertions.assertEquals("aaaaa", resultSet.getString("product_name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void deleteProduct() {
        storageRepository.save(new Product("bbbbbbb", new LinkedList<>(Collections.singletonList("lalala")), 1, 0.0));
        storageRepository.delete("bbbbbbb");

        String query = "SELECT * FROM storage WHERE product_name=?";
        PreparedStatement statement;
        ResultSet resultSet;
        try {
            statement = dbConnection.prepareStatement(query);
            statement.setString(1, "bbbbbbb");
            resultSet = statement.executeQuery();
            Assertions.assertNull(resultSet.getString("product_name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listByAmount() {
        List<Product> productList = new LinkedList<>(Arrays.asList(
                new Product("buckwheat", new LinkedList<>(Collections.singletonList("food")), 10000, 20.0),
                new Product("rice", new LinkedList<>(Collections.singletonList("food")), 5500, 30.50),
                new Product("spaghetti", new LinkedList<>(Collections.singletonList("food")), 2000, 25.7),
                new Product("nut", new LinkedList<>(Collections.singletonList("food")), 7000, 50.7)));

        storageRepository.save(productList);
        ResultSet resultSet = storageRepository.listByAmount(1000, 7000);
        Assertions.assertEquals(3, getResultSetSize(resultSet));
    }

    @Test
    public void listByPrice() {
        List<Product> productList = new LinkedList<>(Arrays.asList(
                new Product("toy1", new LinkedList<>(Arrays.asList("toys", "kids")), 100, 200.0),
                new Product("toy2", new LinkedList<>(Arrays.asList("toys", "kids")), 200, 99.99),
                new Product("toy3", new LinkedList<>(Collections.singletonList("toys")), 50, 250.87),
                new Product("toy4", new LinkedList<>(Collections.singletonList("toys")), 1, 1999.9)));

        storageRepository.save(productList);
        ResultSet resultSet = storageRepository.listByPrice(99.99, 250.0);
        Assertions.assertEquals(2, getResultSetSize(resultSet));
    }

    @Test
    public void findByGroup() {
        ResultSet resultSet = storageRepository.findByGroup("toys");
        Assertions.assertEquals(4, getResultSetSize(resultSet));
    }

    @Test
    public void findProductById() throws SQLException {
        //should fail
      ResultSet resultSet = storageRepository.findProductById(1);
       Assertions.assertEquals("butter", resultSet.getString("product_name"));
    }

    private int getResultSetSize(ResultSet resultSet) {
        int size = 0;
        try {
            while (resultSet.next()) {
                size++;
            }
        } catch (SQLException e) {
            e.printStackTrace();        }
        return size;
    }


    @AfterAll
    public static void tearDown() throws Exception {
        dbConnection.close();
        storageRepository.getConnection().close();
    }

}