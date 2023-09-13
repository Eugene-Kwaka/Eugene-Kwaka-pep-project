package DAO;

// import static org.mockito.Mockito.lenient;

import java.sql.*;
import java.util.*;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDAO {

    // CREATE MESSAGE
    public Optional<Message> createMessage(Message message){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try{
            connection = ConnectionUtil.getConnection();

            // check if message text is not blank and it has atleast 255 characters or less.
            if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255){
                // cannot create a message as it does not satisfy the requirements
                return Optional.empty();
            }

            // checking if an existing user (posted_by) created the message
            if (!userExists(connection, message.getPosted_by())){
                // cannot create a message as user does not exist
                return Optional.empty(); 
            }

            // Write SQL statement to create a message
            String sql = "INSERT INTO message(message_id, posted_by, message_text) VALUES(?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message.getMessage_id());
            preparedStatement.setInt(2, message.getPosted_by());
            preparedStatement.setString(3, message.getMessage_text());
            preparedStatement.executeUpdate();

            // Message created successfully
            return Optional.of(message);        
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty(); // Creation failed (database error)
        } 
        finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // GET ALL MESSAGES
    public List<Message> getAllMessages(){
        // Create a list of all messages to be returned
        List<Message> allMessages = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try{
            connection = ConnectionUtil.getConnection();

            //SQL Query to get all messages
            String sql = "SELECT * FROM message";
            preparedStatement = connection.prepareStatement(sql);
            rs = preparedStatement.executeQuery();

            while (rs.next()){
                int message_id = rs.getInt("message_id");
                int posted_by = rs.getInt("posted_by");
                String message_text = rs.getString("message_text");
                long time_posted_epoch = rs.getLong("time_posted_epoch");

                Message message = new Message(message_id, posted_by, message_text, time_posted_epoch);
                allMessages.add(message);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } 
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Return the list of messages
        return allMessages;
    }

    // GET MESSAGE BY ID
    public Optional<Message> getMessageById(int message_id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try{
            connection = ConnectionUtil.getConnection();

            String sql = "SELECT * FROM message WHERE message_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);

            rs = preparedStatement.executeQuery();
            while(rs.next()){
                //int message_id = rs.getInt("message_id"),
                int posted_by = rs.getInt("posted_by");
                String message_text = rs.getString("message_text");
                long time_posted_epoch = rs.getLong("time_posted_epoch");

                return Optional.of(new Message(message_id, posted_by, message_text, time_posted_epoch));       
            }

            return Optional.empty();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty(); // Database error
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // DELETE MESSAGE    
    public Optional<Message> deleteMessageById(int message_id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        //ResultSet rs = null;

        try{
            connection = ConnectionUtil.getConnection();

            // check if message exists to confirm it can be deleted
            if (!messageExists(connection, message_id)){
                return Optional.empty();
            }


            String sql = "DELETE FROM message WHERE message_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);
            preparedStatement.executeUpdate();

            // display the deleted message
            return getMessageById(message_id);
            
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty(); // Deletion failed (database error)
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // UPDATE MESSAGE
    public Optional<Message> updateMessageById(int message_id, String newMessage){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try{
            connection = ConnectionUtil.getConnection();

            // check if message_id exists
            if (!messageExists(connection, message_id)){
                return Optional.empty();
            }

            // check if the new message_text is not blank and is not over 255
            if (newMessage.isBlank() || newMessage.length() > 255){
                return Optional.empty();
            }

            // Query to update the message
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newMessage);
            preparedStatement.setInt(2, message_id);
            preparedStatement.executeUpdate();

            // display the new updated messsage by its Id
            return getMessageById(message_id);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty(); // Update failed (database error)
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // GET MESSAGE FROM PARTICULAR USER
    public List<Message> getMessageByUser(int account_id){
        // A list of all messages from the specified user
        List<Message> userMessages = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try{
            connection = ConnectionUtil.getConnection();
            // Query
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);
            rs = preparedStatement.executeQuery();

            while(rs.next()){
                int message_id = rs.getInt("message_id");
                int posted_by = rs.getInt("posted_by");
                String message_text = rs.getString("message_text");
                long time_posted_epoch = rs.getLong("time_posted_epoch");

                Message message = new Message(message_id, posted_by, message_text, time_posted_epoch);
                userMessages.add(message);
            }
           
        }
        catch (SQLException e) {
            e.printStackTrace();
        } 
        finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userMessages;
    }

    // userExists method for validation
    // I count the number of users with a specified Id, if the count is 1, then the user exists in the DB
    private boolean userExists(Connection connection, int account_id) throws SQLException {
        String query = "SELECT COUNT(*) FROM accounts WHERE account_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, account_id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }
        return false;
    }

    // messageExists for validation
    // I count the number of messages with a specified message_id, if the count is 1 then that means the message exists in the DB
    private boolean messageExists(Connection connection, int message_id) throws SQLException {
        String query = "SELECT COUNT(*) FROM messages WHERE message_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, message_id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }
        return false;
    }
}
