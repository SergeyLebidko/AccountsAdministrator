package srg.accountsadministrator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import srg.accountsadministrator.dao.Account;
import srg.accountsadministrator.dao.AccountDAO;

public class Login extends HttpServlet {

    private static final String ADMIN_DEFAULT_FIRST_NAME = "Admin";
    private static final String ADMIN_DEFAULT_LAST_NAME = "Admin";
    private static final String ADMIN_DEFAULT_USERNAME = "Administrator";
    private static final String ADMIN_DEFAULT_PASSWORD = "password";

    private AccountDAO accountDAO;
    
    private Connection connection;
    private Statement statement;
    private PreparedStatement checkAdminAccountStmt;
    private PreparedStatement createAdminAccountStmt;
    private PreparedStatement getAdministratorPassword;

    private ServletContext context;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init();

        String jdbcClassName = config.getInitParameter("jdbc_class_name");
        String databasePath = config.getInitParameter("database_path");

        try {
            accountDAO = new AccountDAO(jdbcClassName, databasePath);
        } catch (Exception ex) {
            throw new ServletException("Не удалось создать соединение с базой данных. Ошибка: " + ex.getMessage());
        }

        context = config.getServletContext();
        context.setAttribute("accountDAO", accountDAO);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("Windows-1251");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        //Проверяем наличие в БД учетной записи для администратора. 
        //Если её нет (БД пуста), то создаем её с параметрами по-умолчанию
        try {
            Account adminAccount = accountDAO.getAccount(ADMIN_DEFAULT_USERNAME);
            if (adminAccount==null) {
                accountDAO.createAccount(ADMIN_DEFAULT_FIRST_NAME, ADMIN_DEFAULT_LAST_NAME, ADMIN_DEFAULT_USERNAME, ADMIN_DEFAULT_PASSWORD);
            }
        } catch (SQLException ex) {
            throw new ServletException("Ошибка доступа к базе данных: " + ex.getMessage());
        }

        //Выводим страничку ввода пароля
        out.print("<html>");
        createHeaderLoginPage(out);
        createBodyLoginPage(out, false);
        out.print("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("Windows-1251");
        String inputPassword = (String) request.getParameter("password");

        Boolean adminEntered;
        try {
            //Проверяем введенный пароль
            if (checkInputAdminPassword(inputPassword)) {
                adminEntered = true;
                context.setAttribute("adminEntered", adminEntered);
                response.sendRedirect("accounts");
            } else {
                adminEntered = false;
                context.setAttribute("adminEntered", adminEntered);
                response.setCharacterEncoding("Windows-1251");
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                
                //Выводим страничку ввода пароля
                out.print("<html>");
                createHeaderLoginPage(out);
                createBodyLoginPage(out, true);
                out.print("</html>");
            }
        } catch (SQLException ex) {
            throw new ServletException("Не удалось проверить правильность введенного пароля. Ошибка: "+ex.getMessage());
        }
    }

    private boolean checkInputAdminPassword(String inputPassword) throws SQLException {
        Account adminAccount = accountDAO.getAccount(ADMIN_DEFAULT_USERNAME);
        return adminAccount.equals(inputPassword);
    }

    private void createHeaderLoginPage(PrintWriter out) {
        String header = "<head><title>Accounts Administrator - Login Page</title></head>";
        out.print(header);
    }

    private void createBodyLoginPage(PrintWriter out, boolean isError) {
        out.print("<body><center>");
        String[] normalBody = {
            "<h3>Введите пароль администратора</h3>",
            "<br>",
            "<form action='login' method='post'>",
            "Пароль: <input type='password' name='password' size=15>",
            "<br><br>",
            "<input type='submit' name='submit' value='Войти'>",
            "</form>"
        };
        String[] errorBody = {
            "<h3 style='color: red;'>Пароль неверный. Повторите ввод</h3>",
            "<br>",
            "<form action='login' method='post'>",
            "Пароль: <input type='password' name='password' size=15>",
            "<br><br>",
            "<input type='submit' name='submit' value='Войти'>",
            "</form>"
        };

        String[] body;
        if (isError) {
            body = errorBody;
        } else {
            body = normalBody;
        }
        for (String line : body) {
            out.print(line);
        }
        out.print("</center></body>");
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            accountDAO.dispose();
        } catch (SQLException ex) {}
    }

}
