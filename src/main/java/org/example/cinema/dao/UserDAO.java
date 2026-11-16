package org.example.cinema.dao;

import org.example.cinema.entity.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends DAO {

    private static final String SELECT_SELLER_BY_USERNAME =
        "SELECT u.id, u.fullName, u.phoneNumber, u.email, u.username, u.password, " +
        "       s.staffCode, s.salary, s.hireDate, sl.counterPosition " +
        "FROM tblUser4 u " +
        "JOIN tblStaff4 s ON s.id = u.id " +
        "JOIN tblSeller4 sl ON sl.id = s.id " +
        "WHERE u.username = ? AND u.userType = 'STAFF'";

    public UserDAO() {
    }

    /** Authenticates a counter seller; returns null when the credentials do not match. */
    public Seller authenticateSeller(String username, String rawPassword) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_SELLER_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                if (!rawPassword.equals(rs.getString("password"))) {
                    return null;
                }
                Seller seller = new Seller();
                seller.setId(rs.getInt("id"));
                seller.setFullName(rs.getString("fullName"));
                seller.setPhoneNumber(rs.getString("phoneNumber"));
                seller.setEmail(rs.getString("email"));
                seller.setUsername(rs.getString("username"));
                seller.setStaffCode(rs.getString("staffCode"));
                seller.setSalary(rs.getDouble("salary"));
                java.sql.Date hireDate = rs.getDate("hireDate");
                if (hireDate != null) {
                    seller.setHireDate(hireDate.toLocalDate());
                }
                seller.setCounterPosition(rs.getString("counterPosition"));
                return seller;
            }
        }
    }

}
