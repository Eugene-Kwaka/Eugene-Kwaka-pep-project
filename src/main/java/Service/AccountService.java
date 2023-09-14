package Service;

import Model.Account;
import DAO.APIDAO;
import java.util.*;


public class AccountService {
    public APIDAO apiDAO;

    public AccountService(){
        this.apiDAO = new APIDAO();
    }

    public AccountService(APIDAO apiDAO){
        this.apiDAO = apiDAO;
    }

    

    public Account registration(Account account) {
        // Checking if username is not blank
        if (account.getUsername() == null || account.getUsername().isBlank()) {
            System.out.println("Invalid username");
            return null;
        }

        // Checking if username already exists in the DB
        if (apiDAO.getAccountByUsername(account.getUsername()) != null){
            System.out.println("username exists");
            return null;
        }

        // Checking if the password has at least 4 characters or more
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            System.out.println("password is short");
            return null;
        }
        return apiDAO.registration(account);
    }

    public Account login(Account account) {
        Account attemptLogin = apiDAO.getAccountByUsernameAndPassword(account);
        if (attemptLogin == null){
            System.out.println("invalid credentials");
            return null;
            
        }
        return attemptLogin;
    }
}
