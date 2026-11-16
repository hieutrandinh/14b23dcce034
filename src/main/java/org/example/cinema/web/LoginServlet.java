package org.example.cinema.web;

import org.example.cinema.dao.UserDAO;
import org.example.cinema.entity.Seller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("ViewLogin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            Seller seller = userDAO.authenticateSeller(username, password);
            if (seller == null) {
                request.setAttribute("error", "Invalid username or password.");
                request.setAttribute("username", username);
                request.getRequestDispatcher("ViewLogin.jsp").forward(request, response);
                return;
            }
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(-1);
            session.setAttribute("currentSeller", seller);
            response.sendRedirect(request.getContextPath() + "/seller/ViewHome.jsp");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
