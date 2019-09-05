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

public class AccountRemover extends HttpServlet {

    private ServletContext context;
    private AccountDAO accountDAO;

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

        Integer removedId = Integer.parseInt(request.getParameter("id"));
        try {
            Account adminAccount = accountDAO.getAccount(Login.ADMIN_DEFAULT_USERNAME);
            if(removedId == adminAccount.getId()){
                out.print("<html>");
                createPageHeader(out);
                createFailRemovePageBody(out, "Нельзя удалить аккаунт администратора");
                out.print("</html>");
                return;
            }
            
            accountDAO.removeAccount(removedId);
            out.print("<html>");
            createPageHeader(out);
            createSuccessRemovePageBody(out);
            out.print("</html>");
        } catch (SQLException ex) {
            out.print("<html>");
            createPageHeader(out);
            createFailRemovePageBody(out, "Ошибка базы данных");
            out.print("</html>");
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private void createPageHeader(PrintWriter out) {
        out.print("<head><title>Accounts Administrator - Create Account</title></head>");
    }

    private void createSuccessRemovePageBody(PrintWriter out) {
        out.print("<body>");
        out.print("<center>");
        out.print("<h3>Аккаунт успешно удален</h3>");
        out.print("<a href='accounts'>К списку аккаунтов</a> ");
        out.print("</center>");
        out.print("</body>");
    }

    private void createFailRemovePageBody(PrintWriter out, String msg) {
        out.print("<body>");
        out.print("<center>");
        out.print("<h3>" + msg + "</h3>");
        out.print("<a href='accounts'>К списку аккаунтов</a> ");
        out.print("</center>");
        out.print("</body>");
    }

}
