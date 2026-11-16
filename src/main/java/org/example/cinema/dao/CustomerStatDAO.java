package org.example.cinema.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerStatDAO extends DAO {

    private static final String UPSERT =
        "INSERT INTO tblCustomerStat4 (id, totalSpent, totalTicketsPurchased, numberOfVisits, lastVisitDate, averageSpendPerVisit) " +
        "VALUES (?, ?, ?, 1, CURDATE(), ?) " +
        "ON DUPLICATE KEY UPDATE " +
        "  totalSpent = totalSpent + VALUES(totalSpent), " +
        "  totalTicketsPurchased = totalTicketsPurchased + VALUES(totalTicketsPurchased), " +
        "  numberOfVisits = numberOfVisits + 1, " +
        "  lastVisitDate = CURDATE(), " +
        // totalSpent and numberOfVisits above already hold their NEW values here:
        // MySQL evaluates a multi-column UPDATE/ON DUPLICATE KEY UPDATE SET list left to right.
        "  averageSpendPerVisit = totalSpent / numberOfVisits";

    public CustomerStatDAO() {
    }

    public boolean updateStat(Connection conn, int idCustomer, double amount, int ticketCount) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPSERT)) {
            ps.setInt(1, idCustomer);
            ps.setDouble(2, amount);
            ps.setInt(3, ticketCount);
            ps.setDouble(4, amount);
            return ps.executeUpdate() > 0;
        }
    }
}
