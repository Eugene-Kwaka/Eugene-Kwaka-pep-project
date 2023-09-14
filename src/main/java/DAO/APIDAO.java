package DAO;

import static org.mockito.Mockito.timeout;

// import static org.mockito.Mockito.lenient;

import java.sql.*;
import java.util.*;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

 public class APIDAO {
    // REGISTRATION
    public Account registration(Account account) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = ConnectionUtil.getConnection();
            // Write SQL String to register an account using username and password
            String sql = "INSERT INTO account(username, password) VALUES(?, ?)";
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // PreparedStatement to write the credentials
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            preparedStatement.executeUpdate();

            // Get the auto-generated account_id
            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int account_id = generatedKeys.getInt(1);
                Account newAccount = new Account(account_id, account.getUsername(), account.getPassword()); 
                return newAccount;
                
            }

            // Registration of the account is successful
           

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account getAccountByUsername(String username){
        Connection connection = ConnectionUtil.getConnection();
        try {
            
            String sql = "SELECT * FROM account WHERE username = ?;";
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Account acc = new Account(rs.getInt("account_id"), rs.getString("username"),
                        rs.getString("password"));
                return acc;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Account getAccountById(int account_id){
        Connection connection = ConnectionUtil.getConnection();
        try {
            
            String sql = "SELECT * FROM account WHERE account_id = ?;";
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, account_id);

            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Account acc = new Account(rs.getInt("account_id"), rs.getString("username"),
                        rs.getString("password"));
                return acc;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }


    public Account getAccountByUsernameAndPassword(Account account){
        Connection connection = ConnectionUtil.getConnection();
        try {
            
            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2,account.getPassword());


            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Account acc = new Account(rs.getInt("account_id"), rs.getString("username"),
                        rs.getString("password"));
                return acc;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    // CREATE MESSAGE
    public Message createMessage(Message message){
        // Connection connection = null;
        // PreparedStatement preparedStatement = null;
        // ResultSet generatedKeys = null;

        try{
             Connection connection = ConnectionUtil.getConnection();
            // Write SQL statement to create a message
            String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //preparedStatement.setInt(1, message.getMessage_id());
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());
            preparedStatement.executeUpdate(); 
            
            // Get the message_id
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int message_id = generatedKeys.getInt(1);
                message.setMessage_id(message_id);
                
                return new Message(message_id, message.posted_by, message.message_text, message.time_posted_epoch);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        } 
        return null;
    }


    // GET ALL MESSAGES
    public List<Message> getAllMessages(){
        // Create a list of all messages to be returned
        List<Message> allMessages = new ArrayList<>();

        try{
             Connection connection = ConnectionUtil.getConnection();

            //SQL Query to get all messages
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

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

            // Return the list of messages
            return allMessages;
    }

    

    // GET MESSAGE BY ID
    public Message getMessageById(int message_id){
        try{
            Connection connection = ConnectionUtil.getConnection();

            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);

            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                //int message_id = rs.getInt("message_id"),
                int posted_by = rs.getInt("posted_by");
                String message_text = rs.getString("message_text");
                long time_posted_epoch = rs.getLong("time_posted_epoch");

                // System.out.println(posted_by);
                return new Message(message_id, posted_by, message_text, time_posted_epoch);       
            }

            //return Optional.empty();
        }
        catch (SQLException e) {
            e.printStackTrace();

        } 
        return null;
    }

    // DELETE MESSAGE    
    public Message deleteMessageById(int message_id){
        try{
            Connection connection = ConnectionUtil.getConnection();
            Message savedMsg = getMessageById(message_id);
            
            String sql = "DELETE FROM message WHERE message_id = ?";
           
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);
            int numOfRows = preparedStatement.executeUpdate();
 
            if (numOfRows > 0){
                return savedMsg;
            }
            
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // UPDATE MESSAGE
    public Message updateMessageById(int message_id, String newMessage){

        try{
            Connection connection = ConnectionUtil.getConnection();

            // Query to update the message
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newMessage);
            preparedStatement.setInt(2, message_id);
            preparedStatement.executeUpdate();

            // display the new updated messsage by its Id
            return getMessageById(message_id);
        }
        catch (SQLException e) {
            e.printStackTrace();
        } 
        return null;
    }

    // GET MESSAGE FROM PARTICULAR USER
    public List<Message> getMessageByUser(int account_id){
        // A list of all messages from the specified user
        List<Message> userMessages = new ArrayList<>();

        try{
            Connection connection = ConnectionUtil.getConnection();
            // Query
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);
            ResultSet rs = preparedStatement.executeQuery();

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

        return userMessages;
    }
}

// //     // userExists method for validation
// //     // I count the number of users with a specified Id, if the count is 1, then the user exists in the DB
// //     private boolean userExists(Connection connection, int account_id) throws SQLException {
// //         String query = "SELECT COUNT(*) FROM account WHERE account_id = ?";
// //         PreparedStatement preparedStatement = connection.prepareStatement(query);
// //         preparedStatement.setInt(1, account_id);
// //         ResultSet resultSet = preparedStatement.executeQuery();
// //         if (resultSet.next()) {
// //             int count = resultSet.getInt(1);
// //             return count > 0;
// //         }
// //         return false;
// //     }

// //     // messageExists for validation
// //     // I count the number of messages with a specified message_id, if the count is 1 then that means the message exists in the DB
// //     private boolean messageExists(Connection connection, int message_id) throws SQLException {
// //         String query = "SELECT COUNT(*) FROM message WHERE message_id = ?";
// //         PreparedStatement preparedStatement = connection.prepareStatement(query);
// //         preparedStatement.setInt(1, message_id);
// //         ResultSet resultSet = preparedStatement.executeQuery();
// //         if (resultSet.next()) {
// //             int count = resultSet.getInt(1);
// //             return count > 0;
// //         }
// //         return false;
// //     }
// }
