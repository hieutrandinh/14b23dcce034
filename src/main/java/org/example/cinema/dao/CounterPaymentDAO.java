package org.example.cinema.dao;

import org.example.cinema.entity.CounterPayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

/**
 * CounterPayment4 is a subtype of Payment4, so one payment writes two rows:
 * the shared part into tblPayment4 and the counter-specific part into
 * tblCounterPayment4 (same id).
 */
public class CounterPaymentDAO extends DAO {

    private static final String INSERT_PAYMENT =
        "INSERT INTO tblPayment4 (paymentCode, invoiceId, amount, payAt, status) VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_COUNTER_PAYMENT =
        "INSERT INTO tblCounterPayment4 (id, paymentMethod) VALUES (?, ?)";

    private static final String COUNT_PAYMENTS = "SELECT COUNT(*) FROM tblPayment4";

    public CounterPaymentDAO() {
    }

    public int createPayment(Connection conn, CounterPayment pay) throws SQLException {
        if (pay.getPaymentCode() == null) {
            pay.setPaymentCode("PM" + String.format("%04d", nextSequence(conn)));
        }
        int id;
        try (PreparedStatement ps = conn.prepareStatement(INSERT_PAYMENT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pay.getPaymentCode());
            ps.setInt(2, pay.getInvoiceId());
            ps.setDouble(3, pay.getAmount());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(
                pay.getPayAt() != null ? pay.getPayAt() : LocalDateTime.now()));
            ps.setString(5, pay.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                id = keys.getInt(1);
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(INSERT_COUNTER_PAYMENT)) {
            ps.setInt(1, id);
            ps.setString(2, pay.getPaymentMethod());
            ps.executeUpdate();
        }
        pay.setId(id);
        return id;
    }

    private int nextSequence(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(COUNT_PAYMENTS);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) + 1;
        }
    }
}
