package org.example.cinema.dao;

import org.example.cinema.entity.Seat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * There is no per-show-time seat table: a seat counts as SOLD for a show time
 * exactly when a tblTicket4 row already exists for that (showTimeId, seatId)
 * pair, so the status is derived by joining against tblTicket4.
 */
public class SeatDAO extends DAO {

    private static final String SELECT_SEAT_MAP =
        "SELECT s.id, s.roomId, s.seatRow, s.seatNumber, " +
        "       CASE WHEN t.id IS NOT NULL THEN 'SOLD' ELSE 'AVAILABLE' END AS status " +
        "FROM tblSeat4 s " +
        "JOIN tblShowTimeRoom4 str ON str.roomId = s.roomId " +
        "LEFT JOIN tblTicket4 t ON t.seatId = s.id AND t.showTimeId = str.showTimeId " +
        "WHERE str.showTimeId = ? AND str.roomId = ? ORDER BY s.seatRow, s.seatNumber";

    private static final String SELECT_ONE =
        "SELECT s.id, s.roomId, s.seatRow, s.seatNumber, " +
        "       CASE WHEN t.id IS NOT NULL THEN 'SOLD' ELSE 'AVAILABLE' END AS status " +
        "FROM tblSeat4 s " +
        "JOIN tblShowTimeRoom4 str ON str.roomId = s.roomId " +
        "LEFT JOIN tblTicket4 t ON t.seatId = s.id AND t.showTimeId = str.showTimeId " +
        "WHERE str.showTimeId = ? AND str.roomId = ? AND s.id = ?";

    /** Same check inside the sale transaction; the seat row is locked until commit. */
    private static final String SELECT_ONE_FOR_UPDATE = SELECT_ONE + " FOR UPDATE";

    public SeatDAO() {
    }

    /** All seats of the room that hosts this show time, with their sold/available status. */
    public List<Seat> getSeatMap(int idShowTime, int idRoom) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_SEAT_MAP)) {
            ps.setInt(1, idShowTime);
            ps.setInt(2, idRoom);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapRow(rs));
                }
            }
        }
        return seats;
    }

    /** True when the seat belongs to the room of this show time and has not been sold yet. */
    public boolean checkSeatAvailable(int idShowTime, int idRoom, int idSeat) throws SQLException {
        Seat seat = findOne(idShowTime, idRoom, idSeat);
        return seat != null && !seat.isSold();
    }

    public Seat findOne(int idShowTime, int idRoom, int idSeat) throws SQLException {
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_ONE)) {
            ps.setInt(1, idShowTime);
            ps.setInt(2, idRoom);
            ps.setInt(3, idSeat);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Re-checks availability inside the sale transaction, right before the
     * ticket rows are inserted. The UNIQUE (showTimeId, seatId) key on
     * tblTicket4 is the final guard against two counters selling the same seat.
     */
    public boolean checkSeatAvailable(Connection conn, int idShowTime, int idRoom, int idSeat) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ONE_FOR_UPDATE)) {
            ps.setInt(1, idShowTime);
            ps.setInt(2, idRoom);
            ps.setInt(3, idSeat);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && !Seat.SOLD.equals(rs.getString("status"));
            }
        }
    }

    private Seat mapRow(ResultSet rs) throws SQLException {
        Seat seat = new Seat();
        seat.setId(rs.getInt("id"));
        seat.setRoomId(rs.getInt("roomId"));
        seat.setSeatRow(rs.getString("seatRow"));
        seat.setSeatNumber(rs.getInt("seatNumber"));
        seat.setStatus(rs.getString("status"));
        return seat;
    }
}
