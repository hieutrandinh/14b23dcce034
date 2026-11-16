package org.example.cinema.dao;

import org.example.cinema.entity.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerDAO extends DAO {

    private static final String CHECK_DUPLICATE =
        "SELECT COUNT(*) FROM tblUser4 WHERE phoneNumber = ? OR username = ?";

    private static final String INSERT_USER =
        "INSERT INTO tblUser4 (fullName, phoneNumber, email, username, password, userType) " +
        "VALUES (?, ?, ?, ?, ?, 'CUSTOMER')";

    private static final String INSERT_CUSTOMER =
        "INSERT INTO tblCustomer4 (id, dateOfBirth, address) VALUES (?, ?, ?)";

    public CustomerDAO() {
    }

    /** Returns true if the phone number or username is already registered. */
    public boolean checkDuplicate(String phoneNumber, String username) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_DUPLICATE)) {
            ps.setString(1, phoneNumber);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /** Creates the user + customer rows within the caller's transaction and returns the new customer id. */
    public int createCustomer(Connection conn, Customer cus) throws SQLException {
        int id;
        try (PreparedStatement ps = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cus.getFullName());
            ps.setString(2, cus.getPhoneNumber());
            ps.setString(3, cus.getEmail());
            ps.setString(4, cus.getUsername());
            ps.setString(5, cus.getPassword());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                id = keys.getInt(1);
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(INSERT_CUSTOMER)) {
            ps.setInt(1, id);
            ps.setDate(2, cus.getDateOfBirth() != null ? java.sql.Date.valueOf(cus.getDateOfBirth()) : null);
            ps.setString(3, cus.getAddress());
            ps.executeUpdate();
        }
        return id;
    }
}
