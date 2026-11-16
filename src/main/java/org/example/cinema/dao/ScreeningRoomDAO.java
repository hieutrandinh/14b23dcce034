package org.example.cinema.dao;

import org.example.cinema.entity.ScreeningRoom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScreeningRoomDAO extends DAO {

    private static final String SELECT_ROOMS_BY_SHOW_TIME =
        "SELECT DISTINCT r.id, r.roomName, r.totalSeats, r.seatMap, r.priceMultiplier " +
        "FROM tblScreeningRoom4 r " +
        "JOIN tblShowTimeRoom4 str ON str.roomId = r.id " +
        "JOIN tblShowTime4 st ON st.id = str.showTimeId " +
        "WHERE st.movieId = ? AND st.id = ? ORDER BY r.roomName";

    private static final String SELECT_BY_ID =
        "SELECT id, roomName, totalSeats, seatMap, priceMultiplier FROM tblScreeningRoom4 WHERE id = ?";

    private static final String SELECT_BY_SHOW_TIME_AND_ID =
        "SELECT r.id, r.roomName, r.totalSeats, r.seatMap, r.priceMultiplier " +
        "FROM tblScreeningRoom4 r " +
        "JOIN tblShowTimeRoom4 str ON str.roomId = r.id " +
        "JOIN tblShowTime4 st ON st.id = str.showTimeId " +
        "WHERE st.movieId = ? AND st.id = ? AND r.id = ?";

    public ScreeningRoomDAO() {
    }

    public List<ScreeningRoom> getRoomListByShowTime(int idMovie, int idShowTime) throws SQLException {
        List<ScreeningRoom> rooms = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ROOMS_BY_SHOW_TIME)) {
            ps.setInt(1, idMovie);
            ps.setInt(2, idShowTime);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRow(rs));
                }
            }
        }
        return rooms;
    }

    public ScreeningRoom findByShowTimeAndId(int idMovie, int idShowTime, int idRoom) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_SHOW_TIME_AND_ID)) {
            ps.setInt(1, idMovie);
            ps.setInt(2, idShowTime);
            ps.setInt(3, idRoom);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public ScreeningRoom findById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private ScreeningRoom mapRow(ResultSet rs) throws SQLException {
        ScreeningRoom room = new ScreeningRoom();
        room.setId(rs.getInt("id"));
        room.setRoomName(rs.getString("roomName"));
        room.setTotalSeats(rs.getInt("totalSeats"));
        room.setSeatMap(rs.getString("seatMap"));
        room.setPriceMultiplier(rs.getDouble("priceMultiplier"));
        return room;
    }
}
