package srg.accountsadministrator;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccountEditor extends HttpServlet {

    private ServletContext context;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        context = config.getServletContext();
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
        
        out.print("Сервлет для редактирования аккаунта");
        out.print(" id = "+request.getParameter("id"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}
