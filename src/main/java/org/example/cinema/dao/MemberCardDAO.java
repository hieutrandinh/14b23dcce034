package org.example.cinema.dao;

import org.example.cinema.entity.MemberCard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class MemberCardDAO extends DAO {

    private static final double DISCOUNT_RATE = 0.10;
    private static final int POINTS_PER_UNIT_SPENT = 10000;

    private static final String SELECT_BY_CODE =
        "SELECT id, cardCode, customerId, registrationDate, loyaltyPoint, pointsUsed, pointsBalance " +
        "FROM tblMemberCard4 WHERE cardCode = ?";

    private static final String INSERT_CARD =
        "INSERT INTO tblMemberCard4 (cardCode, customerId, registrationDate, loyaltyPoint, pointsUsed, pointsBalance) " +
        "VALUES (?, ?, ?, 0, 0, 0)";

    private static final String UPDATE_POINTS =
        "UPDATE tblMemberCard4 SET loyaltyPoint = loyaltyPoint + ?, pointsBalance = pointsBalance + ? WHERE id = ?";

    public MemberCardDAO() {
    }

    public String generateCardCode() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM tblMemberCard4");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            int next = rs.getInt(1) + 1;
            return String.format("MC%04d", next);
        }
    }

    /** Creates a member card for the given customer id, connection supplied by the caller's transaction. */
    public MemberCard createMemberCard(Connection conn, int idCustomer) throws SQLException {
        MemberCard card = new MemberCard();
        card.setCardCode(generateCardCode());
        card.setCustomerId(idCustomer);
        card.setRegistrationDate(LocalDate.now());
        try (PreparedStatement ps = conn.prepareStatement(INSERT_CARD, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, card.getCardCode());
            ps.setInt(2, idCustomer);
            ps.setDate(3, java.sql.Date.valueOf(card.getRegistrationDate()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                card.setId(keys.getInt(1));
            }
        }
        return card;
    }

    public MemberCard checkMemberCard(String cardCode) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_CODE)) {
            ps.setString(1, cardCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapRow(rs);
            }
        }
    }

    public double calculateDiscount(MemberCard card, double total) {
        if (card == null) {
            return 0;
        }
        return Math.round(total * DISCOUNT_RATE);
    }

    public boolean updateLoyaltyPoint(Connection conn, int idCard, double amount) throws SQLException {
        int earnedPoints = (int) (amount / POINTS_PER_UNIT_SPENT);
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_POINTS)) {
            ps.setInt(1, earnedPoints);
            ps.setInt(2, earnedPoints);
            ps.setInt(3, idCard);
            return ps.executeUpdate() > 0;
        }
    }

    private MemberCard mapRow(ResultSet rs) throws SQLException {
        MemberCard card = new MemberCard();
        card.setId(rs.getInt("id"));
        card.setCardCode(rs.getString("cardCode"));
        card.setCustomerId(rs.getInt("customerId"));
        java.sql.Date regDate = rs.getDate("registrationDate");
        if (regDate != null) {
            card.setRegistrationDate(regDate.toLocalDate());
        }
        card.setLoyaltyPoint(rs.getInt("loyaltyPoint"));
        card.setPointsUsed(rs.getInt("pointsUsed"));
        card.setPointsBalance(rs.getInt("pointsBalance"));
        return card;
    }
}
