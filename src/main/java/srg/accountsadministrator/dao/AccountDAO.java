package srg.accountsadministrator.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class AccountDAO {

    private Connection connection;
    
    private PreparedStatement getAccountsByIdStmt;
    private PreparedStatement getAccountsByUsernameStmt;
    private PreparedStatement createAccountStmt;
    private PreparedStatement updateAccountStmt;
    private PreparedStatement getAllAccountsStmt;
    private PreparedStatement removeAccountStmt;

    public AccountDAO(String jdbcClassName, String databasePath) throws SQLException, ClassNotFoundException {
        Class.forName(jdbcClassName);
        connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        
        getAccountsByIdStmt = connection.prepareStatement("SELECT * FROM ACCOUNTS WHERE ID=?");
        getAccountsByUsernameStmt = connection.prepareStatement("SELECT * FROM ACCOUNTS WHERE USERNAME=?");
        createAccountStmt = connection.prepareStatement("INSERT INTO ACCOUNTS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD) VALUES (?, ?, ?, ?)");
        updateAccountStmt = connection.prepareStatement("UPDATE ACCOUNTS SET FIRST_NAME=?, LAST_NAME=?, USERNAME=?, PASSWORD=? WHERE ID=?");
        getAllAccountsStmt = connection.prepareStatement("SELECT * FROM ACCOUNTS ORDER BY USERNAME");
        removeAccountStmt = connection.prepareStatement("DELETE FROM ACCOUNTS WHERE ID=?");
    }
    
    public Account getAccount(int id) throws SQLException{
        getAccountsByIdStmt.setInt(1, id);
        ResultSet resultSet = getAccountsByIdStmt.executeQuery();
        if(resultSet.isClosed())return null;
        
        String firstName = resultSet.getString(2);
        String lastName = resultSet.getString(3);
        String username = resultSet.getString(4);
        String password = resultSet.getString(5);
        
        return new Account(id, firstName, lastName, username, password);
    }
    
    public Account getAccount(String username) throws SQLException{
        getAccountsByUsernameStmt.setString(1, username);
        ResultSet resultSet = getAccountsByUsernameStmt.executeQuery();
        if(resultSet.isClosed())return null;
        
        Integer id = resultSet.getInt(1);
        String firstName = resultSet.getString(2);
        String lastName = resultSet.getString(3);
        String password = resultSet.getString(5);
                
        return new Account(id, firstName, lastName, username, password);
    }
    
    public List<Account> getAllAccounts() throws SQLException{
        List<Account> list = new LinkedList<>();
        ResultSet resultSet = getAllAccountsStmt.executeQuery();
        
        int id;
        String firstName;
        String lastName;
        String username;
        String password;
        
        while(resultSet.next()){
            id = resultSet.getInt(1);
            firstName = resultSet.getString(2);
            lastName = resultSet.getString(3);
            username = resultSet.getString(4);
            password = resultSet.getString(5);
            list.add(new Account(id, firstName, lastName, username, password));
        }
        
        return list;
    }
    
    public void createAccount(String firstName, String lastName, String username, String password) throws SQLException{
        createAccountStmt.setString(1, firstName);
        createAccountStmt.setString(2, lastName);
        createAccountStmt.setString(3, username);
        createAccountStmt.setString(4, password);
        
        createAccountStmt.executeUpdate();
    }
    
    public void updateAccount(Integer id, String firstName, String lastName, String username, String password) throws SQLException{
        updateAccountStmt.setString(1, firstName);
        updateAccountStmt.setString(2, lastName);
        updateAccountStmt.setString(3, username);
        updateAccountStmt.setString(4, password);
        updateAccountStmt.setInt(5, id);
        updateAccountStmt.executeUpdate();
    }
    
    public void removeAccount(int id) throws SQLException{
        removeAccountStmt.setInt(1, id);
        removeAccountStmt.executeUpdate();
    }
    
    public void dispose() throws SQLException{
        getAccountsByIdStmt.close();
        getAccountsByUsernameStmt.close();
        createAccountStmt.close();
        updateAccountStmt.close();
        getAllAccountsStmt.close();
        removeAccountStmt.close();
        connection.close();
    }

}
