package srg.accountsadministrator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import srg.accountsadministrator.dao.Account;
import srg.accountsadministrator.dao.AccountDAO;

public class AccountsList extends HttpServlet {

    private ServletContext context;
    private AccountDAO accountDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        context = config.getServletContext();
        
        accountDAO = (AccountDAO)context.getAttribute("accountDAO");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("Windows-1251");
        response.setContentType("text/html");

        //Проверяем, залогинился ли администратор. И если нет, то переводим пользователя на страничку логина
        Boolean adminEntered = (Boolean) context.getAttribute("adminEntered");
        if(adminEntered==null || !adminEntered){
            response.sendRedirect("login");
            return;
        }
        
        PrintWriter out = response.getWriter();
        out.print("<html>");
        createPageHeader(out);
        createPageBody(out);
        out.print("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private void createPageHeader(PrintWriter out){
        out.print("<head><title>Accounts Administrator - Accounts List</title></head>");
    }
    
    private void createPageBody(PrintWriter out){
        out.print("<body><center>");
        
        List<Account> list;
        try {
            list = accountDAO.getAllAccounts();
        } catch (SQLException ex) {
            out.print("<h2>Не удалось получить список аккаунтов</h2>");
            return;
        }
        
        out.print("<h3>Список аккаунтов</h3>");
        out.print("<br>");
        out.print("<table cellpadding=15>");
        out.print("<tr bgcolor=#DDDDDD><td><b>Имя</b></td><td><b>Фамилия</b></td><td><b>Логин</b></td><td><b>Пароль</b></td><td><b></b></td></tr>");
        
        int lineNumber = 0;
        String color1="#E6E6E6";
        String color2="#F6F6F6";
        
        for(Account account: list){
            lineNumber++;
            if(lineNumber%2!=0){
                out.print("<tr bgcolor="+color1+">");
            }else{
                out.print("<tr bgcolor="+color2+">");
            }
            out.print("<td>"+account.getFirstName()+"</td>");
            out.print("<td>"+account.getLastName()+"</td>");
            out.print("<td>"+account.getUsername()+"</td>");
            out.print("<td>"+account.getPassword()+"</td>");
            out.print("<td>");
            out.print("<a href='edit_account'>Изменить</a>");
            out.print("&nbsp&nbsp");
            out.print("<a href='remove_account'>Удалить</a>");
            out.print("</td>");
            out.print("</tr>");
        }

        out.print("<tr>");
        out.print("<td colspan=3>");
        out.print("<a href='create_account'>Добавить аккаунт</a>");
        out.print("<br>");
        out.print("<a href='logout'>Выход</a>");
        out.print("</td>");
        out.print("</tr>");
        out.print("</table>");
        
        out.print("</center></body>");
    }
    
}
