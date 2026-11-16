package org.example.cinema.dao;

import org.example.cinema.entity.Invoice;
import org.example.cinema.entity.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO extends DAO {

    private static final String INSERT_INVOICE =
        "INSERT INTO tblInvoice4 (invoiceCode, customerId, sellerId, issuedDate, totalAmount, discountAmount, finalAmount) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_INVOICE =
        "SELECT id, invoiceCode, customerId, sellerId, issuedDate, totalAmount, discountAmount, finalAmount " +
        "FROM tblInvoice4 WHERE id = ?";

    private static final String SELECT_TICKETS_BY_INVOICE =
        "SELECT t.id, t.invoiceId, t.showTimeId, t.seatId, t.price, " +
        "       s.seatRow, s.seatNumber, m.name AS movieName, r.roomName, st.showDate, st.startTime " +
        "FROM tblTicket4 t " +
        "JOIN tblSeat4 s ON s.id = t.seatId " +
        "JOIN tblShowTime4 st ON st.id = t.showTimeId " +
        "JOIN tblMovie4 m ON m.id = st.movieId " +
        "JOIN tblScreeningRoom4 r ON r.id = s.roomId " +
        "WHERE t.invoiceId = ? ORDER BY s.seatRow, s.seatNumber";

    public InvoiceDAO() {
    }

    public int createInvoice(Connection conn, Invoice invoice) throws SQLException {
        String code = "HD" + String.format("%04d", nextSequence(conn));
        try (PreparedStatement ps = conn.prepareStatement(INSERT_INVOICE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            if (invoice.getCustomerId() != null) {
                ps.setInt(2, invoice.getCustomerId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, invoice.getSellerId());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(invoice.getIssuedDate() != null ? invoice.getIssuedDate() : LocalDateTime.now()));
            ps.setDouble(5, invoice.getTotalAmount());
            ps.setDouble(6, invoice.getDiscountAmount());
            ps.setDouble(7, invoice.getFinalAmount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);
                invoice.setId(id);
                invoice.setInvoiceCode(code);
                return id;
            }
        }
    }

    public Invoice getInvoiceById(int idInvoice) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_INVOICE)) {
            ps.setInt(1, idInvoice);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Invoice invoice = new Invoice();
                invoice.setId(rs.getInt("id"));
                invoice.setInvoiceCode(rs.getString("invoiceCode"));
                int customerId = rs.getInt("customerId");
                invoice.setCustomerId(rs.wasNull() ? null : customerId);
                invoice.setSellerId(rs.getInt("sellerId"));
                invoice.setIssuedDate(rs.getTimestamp("issuedDate").toLocalDateTime());
                invoice.setTotalAmount(rs.getDouble("totalAmount"));
                invoice.setDiscountAmount(rs.getDouble("discountAmount"));
                invoice.setFinalAmount(rs.getDouble("finalAmount"));
                return invoice;
            }
        }
    }

    public List<Ticket> getTicketsByInvoice(int idInvoice) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_TICKETS_BY_INVOICE)) {
            ps.setInt(1, idInvoice);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ticket t = new Ticket();
                    t.setId(rs.getInt("id"));
                    t.setInvoiceId(rs.getInt("invoiceId"));
                    t.setShowTimeId(rs.getInt("showTimeId"));
                    t.setSeatId(rs.getInt("seatId"));
                    t.setPrice(rs.getDouble("price"));
                    t.setSeatLabel(rs.getString("seatRow") + rs.getInt("seatNumber"));
                    t.setMovieName(rs.getString("movieName"));
                    t.setRoomName(rs.getString("roomName"));
                    t.setShowDateTime(rs.getDate("showDate") + " " + rs.getTime("startTime"));
                    tickets.add(t);
                }
            }
        }
        return tickets;
    }

    private int nextSequence(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM tblInvoice4");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) + 1;
        }
    }
}
