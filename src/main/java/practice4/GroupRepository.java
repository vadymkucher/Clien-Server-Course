package practice4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GroupRepository {
    private Connection connection;

    public GroupRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveGroup(String groupName) {
        String query = "INSERT INTO groups(group_name) VALUES(?)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, groupName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkGroup(String groupName) {
        String query = "SELECT * FROM groups WHERE group_name=?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, groupName);
            ResultSet res = statement.executeQuery();
            if (!res.next()) {
               saveGroup(groupName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveGroups(List<String> groupNames) {
        for (String groupName : groupNames) {
           checkGroup(groupName);
        }
    }

    public ResultSet findGroup(String groupName) {
        PreparedStatement statement;
        try {
            String query = "SELECT * FROM groups WHERE group_name=?";
            statement = connection.prepareStatement(query);
            statement.setString(1, groupName);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet findGroupById(long groupId) {
        PreparedStatement statement;
        try {
            String query = "SELECT * FROM groups WHERE group_id=?";
            statement = connection.prepareStatement(query);
            statement.setLong(1, groupId);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
