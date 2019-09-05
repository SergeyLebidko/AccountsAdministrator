package srg.accountsadministrator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import srg.accountsadministrator.dao.Account;
import srg.accountsadministrator.dao.AccountDAO;

public class AccountEditor extends HttpServlet {

    private ServletContext context;
    private AccountDAO accountDAO;
    private Account editedAccount;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        context = config.getServletContext();
        accountDAO = (AccountDAO) context.getAttribute("accountDAO");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("Windows-1251");
        response.setContentType("text/html");

        //Проверяем, залогинился ли администратор. И если нет, то переводим пользователя на страничку логина
        Boolean adminEntered = (Boolean) context.getAttribute("adminEntered");
        if (adminEntered == null || !adminEntered) {
            response.sendRedirect("login");
            return;
        }

        PrintWriter out = response.getWriter();

        Integer editedId = Integer.parseInt(request.getParameter("id"));
        try {
            editedAccount = accountDAO.getAccount(editedId);

            out.print("<html>");
            createPageHeader(out);
            createPageBody(out, editedAccount, null);
            out.print("</html>");
        } catch (SQLException ex) {
            out.print("<html>");
            createPageHeader(out);
            createFailPageBody(out, "Не удалось получить данные из БД");
            out.print("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("Windows-1251");
        response.setContentType("text/html");
        response.setCharacterEncoding("Windows-1251");

        boolean isCancel = request.getParameter("cancel") != null;
        if (isCancel) {
            response.sendRedirect("accounts");
            return;
        }

        PrintWriter out = response.getWriter();

        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (firstName.equals("") || lastName.equals("") || username.equals("") || password.equals("")) {
            out.print("<html>");
            createPageHeader(out);
            createPageBody(out, editedAccount, "Пустые поля недопустимы!");
            out.print("</html>");
            return;
        }

        if (editedAccount.getUsername().equals(Login.ADMIN_DEFAULT_USERNAME)) {
            if (!username.equals(Login.ADMIN_DEFAULT_USERNAME)) {
                out.print("<html>");
                createPageHeader(out);
                createPageBody(out, editedAccount, "Нельзя изменять логин администратора!");
                out.print("</html>");
                return;
            }
        }
        
        try {
            accountDAO.updateAccount(editedAccount.getId(), firstName, lastName, username, password);
            
            out.print("<html>");
            createPageHeader(out);
            createSuccessPageBody(out);
            out.print("</html>");
            return;
        } catch (SQLException ex) {
            out.print("<html>");
            createPageHeader(out);
            createFailPageBody(out, "Не удалось обновить данные аккаунта");
            out.print("</html>");
        }
    }

    private void createPageHeader(PrintWriter out) {
        out.print("<head><title>Accounts Administrator - Edit Account</title></head>");
    }

    private void createPageBody(PrintWriter out, Account account, String msg) {
        out.print("<body>");
        out.print("<center>");
        out.print("<h3>Введите новые данные для акаунта</h3>");
        if (msg != null) {
            out.print("<h3 style='color: red;'>" + msg + "</h3>");
        }
        out.print("<form name='edit_form' action='edit_account' method='post'>");

        out.print("<table cellpadding=5>");
        out.print("<tr><td>Имя<td><td><input type='text' name='first_name' size=25 value='" + account.getFirstName() + "'><td></tr>");
        out.print("<tr><td>Фамилия<td><td><input type='text' name='last_name' size=25 value='" + account.getLastName() + "'><td></tr>");
        out.print("<tr><td>Логин<td><td><input type='text' name='username' size=25 value='" + account.getUsername() + "'><td></tr>");
        out.print("<tr><td>Пароль<td><td><input type='text' name='password' size=25 value='" + account.getPassword() + "'><td></tr>");
        out.print("</table>");

        out.print("<input type='submit' name='update' value='Обновить'> ");
        out.print("<input type='submit' name='cancel' value='Отмена'>");

        out.print("</form>");
        out.print("</center>");
        out.print("</body>");
    }

    private void createFailPageBody(PrintWriter out, String msg) {
        out.print("<body>");
        out.print("<center>");
        out.print("<h3>" + msg + "</h3>");
        out.print("<a href='accounts'>К списку аккаунтов</a> ");
        out.print("</center>");
        out.print("</body>");
    }

    private void createSuccessPageBody(PrintWriter out){
        out.print("<body>");
        out.print("<center>");
        out.print("<h3>Учетная запись успешно обновлена</h3>");
        out.print("<a href='accounts'>К списку аккаунтов</a> ");
        out.print("</center>");
        out.print("</body>");
    }
        
}
