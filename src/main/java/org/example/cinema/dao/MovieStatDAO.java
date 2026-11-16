package org.example.cinema.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MovieStatDAO extends DAO {

    private static final String UPSERT =
        "INSERT INTO tblMovieStat4 (id, totalTicketsSold, totalRevenue, viewerCount, showCount, occupancyRate) " +
        "VALUES (?, ?, ?, ?, 0, 0) " +
        "ON DUPLICATE KEY UPDATE " +
        "  totalTicketsSold = totalTicketsSold + VALUES(totalTicketsSold), " +
        "  totalRevenue = totalRevenue + VALUES(totalRevenue), " +
        "  viewerCount = viewerCount + VALUES(viewerCount)";

    public MovieStatDAO() {
    }

    public boolean updateStat(Connection conn, int idMovie, int tickets, double revenue) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPSERT)) {
            ps.setInt(1, idMovie);
            ps.setInt(2, tickets);
            ps.setDouble(3, revenue);
            ps.setInt(4, tickets);
            return ps.executeUpdate() > 0;
        }
    }
}
