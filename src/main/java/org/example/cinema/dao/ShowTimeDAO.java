package org.example.cinema.dao;

import org.example.cinema.entity.ShowTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShowTimeDAO extends DAO {

    private static final String SELECT_LIST =
        "SELECT st.id, st.movieId, st.showDate, st.startTime, st.priceMultiplier, " +
        "       m.name AS movieName " +
        "FROM tblShowTime4 st " +
        "JOIN tblMovie4 m ON m.id = st.movieId " +
        "WHERE st.movieId = ? ORDER BY st.showDate, st.startTime";

    private static final String SELECT_BY_ID =
        "SELECT st.id, st.movieId, st.showDate, st.startTime, st.priceMultiplier, " +
        "       m.name AS movieName " +
        "FROM tblShowTime4 st " +
        "JOIN tblMovie4 m ON m.id = st.movieId " +
        "WHERE st.id = ?";

    private static final String SELECT_LIST_BY_DATE =
        "SELECT st.id, st.movieId, st.showDate, st.startTime, st.priceMultiplier, " +
        "       m.name AS movieName " +
        "FROM tblShowTime4 st " +
        "JOIN tblMovie4 m ON m.id = st.movieId " +
        "WHERE st.movieId = ? AND st.showDate = ? " +
        "ORDER BY st.showDate, st.startTime";

    public ShowTimeDAO() {
    }

    public List<ShowTime> getShowTimeList(int idMovie) throws SQLException {
        List<ShowTime> list = new ArrayList<>();
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_LIST)) {
            ps.setInt(1, idMovie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<ShowTime> getShowTimeList(int idMovie, LocalDate showDate) throws SQLException {
        List<ShowTime> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_LIST_BY_DATE)) {
            ps.setInt(1, idMovie);
            ps.setDate(2, Date.valueOf(showDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public ShowTime findById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private ShowTime mapRow(ResultSet rs) throws SQLException {
        ShowTime st = new ShowTime();
        st.setId(rs.getInt("id"));
        st.setMovieId(rs.getInt("movieId"));
        st.setShowDate(rs.getDate("showDate").toLocalDate());
        st.setStartTime(rs.getTime("startTime").toLocalTime());
        st.setPriceMultiplier(rs.getDouble("priceMultiplier"));
        st.setMovieName(rs.getString("movieName"));
        return st;
    }
}
