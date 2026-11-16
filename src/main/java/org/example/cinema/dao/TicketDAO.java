package org.example.cinema.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TicketDAO extends DAO {

    private static final String SELECT_PRICE_BY_SEAT =
        "SELECT m.basePrice, st.priceMultiplier AS showMultiplier, r.priceMultiplier AS roomMultiplier " +
        "FROM tblShowTime4 st " +
        "JOIN tblMovie4 m ON m.id = st.movieId " +
        "JOIN tblShowTimeRoom4 str ON str.showTimeId = st.id " +
        "JOIN tblSeat4 s ON s.roomId = str.roomId " +
        "JOIN tblScreeningRoom4 r ON r.id = s.roomId " +
        "WHERE st.id = ? AND s.id = ?";

    private static final String SELECT_PRICE_BY_ROOM =
        "SELECT m.basePrice, st.priceMultiplier AS showMultiplier, r.priceMultiplier AS roomMultiplier " +
        "FROM tblShowTime4 st " +
        "JOIN tblMovie4 m ON m.id = st.movieId " +
        "JOIN tblShowTimeRoom4 str ON str.showTimeId = st.id " +
        "JOIN tblScreeningRoom4 r ON r.id = str.roomId " +
        "WHERE st.id = ? AND r.id = ?";

    private static final String INSERT_TICKET =
        "INSERT INTO tblTicket4 (invoiceId, showTimeId, seatId, price) VALUES (?, ?, ?, ?)";

    public TicketDAO() {
    }

    public double calculatePrice(int idShowTime, int idSeat) throws SQLException {
        return queryPrice(SELECT_PRICE_BY_SEAT, idShowTime, idSeat);
    }

    public double calculatePriceForRoom(int idShowTime, int idRoom) throws SQLException {
        return queryPrice(SELECT_PRICE_BY_ROOM, idShowTime, idRoom);
    }

    private double queryPrice(String sql, int idShowTime, int relatedId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idShowTime);
            ps.setInt(2, relatedId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Show time/room combination not found: " + idShowTime);
                }
                return rs.getDouble("basePrice") * rs.getDouble("showMultiplier") * rs.getDouble("roomMultiplier");
            }
        }
    }

    public int createTicket(Connection conn, int idInvoice, int idShowTime, int idSeat, double price)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_TICKET, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idInvoice);
            ps.setInt(2, idShowTime);
            ps.setInt(3, idSeat);
            ps.setDouble(4, price);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }
}
