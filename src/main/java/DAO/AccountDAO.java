package DAO;

import Model.Account;
import Util.ConnectionUtil;
import java.sql.*;
import java.util.*;


public class AccountDAO {
    // REGISTRATION
    public Optional<Account> registration(Account account) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = ConnectionUtil.getConnection();

            // Checking if username is not blank
            if (account.getUsername().isBlank()) {
                return Optional.empty();
            }

            // Checking if username already exists in the DB
            if (usernameExists(connection, account.getUsername())) {
                return Optional.empty();
            }

            // Checking if the password has at least 4 characters or more
            if (account.getPassword().length() < 4) {
                return Optional.empty();
            }

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
                account.setAccount_id(account_id);
            }

            // Registration of the account is successful
            return Optional.of(account);

        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty(); // Registration failed (database error)
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
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

    // LOGIN
    public Optional<Account> login(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = ConnectionUtil.getConnection();

            // Check if the username entered already exists in DB
            // If not, return that the Optional container is empty
            if (!usernameExists(connection, username)) {
                return Optional.empty();
            }

            // Get the account from DB
            String sql = "SELECT * FROM account WHERE username=? AND password=?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int account_id = resultSet.getInt("account_id");
                String storedPassword = resultSet.getString("password");
                if (storedPassword.equals(password)) {
                    return Optional.of(new Account(account_id, username, password));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
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

    // usernameExists() method to check if a username already exists during registration and login
    private boolean usernameExists(Connection connection, String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM account WHERE username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }
        return false;
    }

//     // REGISTRATION
//     public Optional<Account> registration(Account account){
//         Connection connection = null;
//         PreparedStatement preparedStatement = null;
//         ResultSet generatedKeys = null;

//         try{
//             connection = ConnectionUtil.getConnection();

//             // checking if username is not blank
//             if (account.getUsername().isBlank()){
//                 return Optional.empty();
//             }

//             // checking if username already exists in the DB
//             if (usernameExists(connection, account.getUsername())){
//                 return Optional.empty();
//             }

//             // checking if password has atleast 4 characters or more
//             if (account.getPassword().length() < 4){
//                 return Optional.empty();
//             }

//             // Write SQL String to register account using username and password
//             String sql = "INSERT INTO account(username, password) VALUES(?, ?)";
//             preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

//             // preparedStatement to write the credentials
//             preparedStatement.setString(1, account.getUsername());
//             preparedStatement.setString(2, account.getPassword());
//             preparedStatement.executeUpdate();

//             // get the autogenerated account_id
//             generatedKeys = preparedStatement.getGeneratedKeys();
//             if(generatedKeys.next()){
//                 int account_id = generatedKeys.getInt(1);
//                 account.setAccount_id(account_id);
//             }
            
//             // Registration of account is successful
//             return Optional.of(account);

//         } 
//         catch (SQLException e) {
//             e.printStackTrace();
//             return Optional.empty(); // Registration failed (database error)
//         } 
//         finally {
//             try {
//                 if (generatedKeys != null) {
//                     generatedKeys.close();
//                 }
//                 if (preparedStatement != null) {
//                     preparedStatement.close();
//                 }
//                 if (connection != null) {
//                     connection.close();
//                 }
//             } catch (SQLException e) {
//                 e.printStackTrace();
//             }
//         }
//     }

//     // LOGIN
//     public Optional<Account> login(String username, String password){
//         Connection connection = null;
//         PreparedStatement preparedStatement = null;

//         try{
//             connection = ConnectionUtil.getConnection();

//             // check if username entered already exists in DB
//             // if not, return that the Optional container is empty
//             if (!usernameExists(connection, username)){
//                 return Optional.empty();
//             }

//             // Get account from DB
//             String sql = "SELECT * FROM account WHERE username=? AND password=?";
//             preparedStatement = connection.prepareStatement(sql);
//             preparedStatement.setString(1, username);
//             ResultSet resultSet = preparedStatement.executeQuery();

//             if(resultSet.next()){
//                 int account_id = resultSet.getInt("account_id");
//                 String storedPassword = resultSet.getString(password);
//                 if(storedPassword.equals(password)){
//                     return Optional.of(new Account(account_id, username, password));
//                 }
//             }

//             return Optional.empty();

//         } catch (SQLException e){
//             e.printStackTrace();
//             return Optional.empty();
//         } finally {
//             try {
//                 if (preparedStatement != null) {
//                     preparedStatement.close();
//                 }
//                 if (connection != null) {
//                     connection.close();
//                 }
//             } catch (SQLException e) {
//                 e.printStackTrace();
//             }
//         }
//     }

//     // usernameExists() method to check if a username already exists during registration and login
//     private boolean usernameExists(Connection connection, String username) throws SQLException {
//         String query = "SELECT COUNT(*) FROM accounts WHERE username = ?";
//         PreparedStatement preparedStatement = connection.prepareStatement(query);
//         preparedStatement.setString(1, username);
//         ResultSet resultSet = preparedStatement.executeQuery();
//         if (resultSet.next()) {
//             int count = resultSet.getInt(1);
//             return count > 0;
//         }
//         return false;
//     }
}
