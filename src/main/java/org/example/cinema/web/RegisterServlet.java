package org.example.cinema.web;

import org.example.cinema.dao.CustomerDAO;
import org.example.cinema.dao.MemberCardDAO;
import org.example.cinema.entity.Customer;
import org.example.cinema.entity.MemberCard;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Module 1: Customer registers as a member. */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final DateTimeFormatter DOB_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final MemberCardDAO memberCardDAO = new MemberCardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/customer/ViewRegister.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = trim(request.getParameter("fullName"));
        String dobText = trim(request.getParameter("dateOfBirth"));
        String phoneNumber = trim(request.getParameter("phoneNumber"));
        String email = trim(request.getParameter("email"));
        String address = trim(request.getParameter("address"));
        String username = trim(request.getParameter("username"));
        String password = request.getParameter("password");

        String error = validate(fullName, dobText, phoneNumber, username, password);
        if (error == null) {
            try {
                if (customerDAO.checkDuplicate(phoneNumber, username)) {
                    error = "Phone number is already registered. Please check again or sign in.";
                }
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("fullName", fullName);
            request.setAttribute("dateOfBirth", dobText);
            request.setAttribute("phoneNumber", phoneNumber);
            request.setAttribute("email", email);
            request.setAttribute("address", address);
            request.setAttribute("username", username);
            request.getRequestDispatcher("/customer/ViewRegister.jsp").forward(request, response);
            return;
        }

        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setDateOfBirth(LocalDate.parse(dobText, DOB_FORMAT));
        customer.setPhoneNumber(phoneNumber);
        customer.setEmail(email);
        customer.setAddress(address);
        customer.setUsername(username);
        customer.setPassword(password);

        try (Connection conn = customerDAO.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int customerId = customerDAO.createCustomer(conn, customer);
                MemberCard card = memberCardDAO.createMemberCard(conn, customerId);
                conn.commit();
                request.setAttribute("fullName", fullName);
                request.setAttribute("card", card);
                request.getRequestDispatcher("/customer/ViewRegisterSuccess.jsp").forward(request, response);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String validate(String fullName, String dobText, String phoneNumber, String username, String password) {
        if (isBlank(fullName)) {
            return "Full name is required.";
        }
        if (isBlank(dobText)) {
            return "Date of birth is required.";
        }
        try {
            LocalDate.parse(dobText, DOB_FORMAT);
        } catch (DateTimeParseException e) {
            return "Date of birth must be in dd/MM/yyyy format.";
        }
        if (isBlank(phoneNumber) || !phoneNumber.matches("\\d{10}")) {
            return "Phone number must be exactly 10 digits.";
        }
        if (isBlank(username) || username.length() < 6 || username.length() > 20) {
            return "Username must be 6-20 characters.";
        }
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters.";
        }
        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.isEmpty();
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
