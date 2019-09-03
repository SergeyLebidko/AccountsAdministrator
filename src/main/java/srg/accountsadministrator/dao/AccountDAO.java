package srg.accountsadministrator.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

    private Connection connection;
    private PreparedStatement getAccountsByIdStmt;
    private PreparedStatement getAccountsByUsernameStmt;
    private PreparedStatement createAccountStmt;

    public AccountDAO(String jdbcClassName, String databasePath) throws SQLException, ClassNotFoundException {
        Class.forName(jdbcClassName);
        connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        
        getAccountsByIdStmt = connection.prepareStatement("SELECT * FROM ACCOUNTS WHERE ID=?");
        getAccountsByUsernameStmt = connection.prepareStatement("SELECT * FROM ACCOUNTS WHERE USERNAME=?");
        createAccountStmt = connection.prepareStatement("INSERT INTO ACCOUNTS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD) VALUES (?, ?, ?, ?)");
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
    
    public void createAccount(String firstName, String lastName, String username, String password) throws SQLException{
        createAccountStmt.setString(1, firstName);
        createAccountStmt.setString(2, lastName);
        createAccountStmt.setString(3, username);
        createAccountStmt.setString(4, password);
        
        createAccountStmt.executeUpdate();
    }
    
    public void dispose() throws SQLException{
        getAccountsByIdStmt.close();
        getAccountsByUsernameStmt.close();
        connection.close();
    }

}
