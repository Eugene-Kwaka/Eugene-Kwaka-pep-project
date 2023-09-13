package Service;

import Model.Account;
import DAO.AccountDAO;
import java.util.*;


public class AccountService {
    public AccountDAO accountDAO;

    public AccountService(){
        this.accountDAO = new AccountDAO();
    }

    public Optional<Account> registration(Account account) {
        return accountDAO.registration(account);
    }

    public Optional<Account> login(String username, String password) {
        return accountDAO.login(username, password);
    }
}
