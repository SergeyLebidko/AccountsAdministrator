package srg.accountsadministrator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Login extends HttpServlet {

    private Connection connection;
    private Statement statement;

    public void init(ServletConfig config) throws ServletException {
        super.init();

        String jdbcClassName = config.getInitParameter("jdbc_class_name");
        String databasePath = config.getInitParameter("database_path");

        try {
            Class.forName(jdbcClassName);
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            statement = connection.createStatement();
        } catch (ClassNotFoundException ex) {
            throw new ServletException("Не удалось загрузить класс драйвера JDBC");
        } catch (SQLException ex) {
            throw new ServletException("Не удалось создать соединение с базой данных. Ошибка: "+ex.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            statement.close();
            connection.close();
        } catch (SQLException ex) {
        }
    }

}
