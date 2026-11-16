package org.example.cinema.dao;

import org.example.cinema.entity.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO extends DAO {

    private static final String SEARCH_MOVIE =
        "SELECT id, name, genre, director, basePrice, duration FROM tblMovie4 WHERE name LIKE ? ORDER BY name";

    private static final String SELECT_BY_ID =
        "SELECT id, name, genre, director, basePrice, duration FROM tblMovie4 WHERE id = ?";

    public MovieDAO() {
    }

    public List<Movie> searchMovie(String name) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH_MOVIE)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapRow(rs));
                }
            }
        }
        return movies;
    }

    public Movie findById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    private Movie mapRow(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setName(rs.getString("name"));
        movie.setGenre(rs.getString("genre"));
        movie.setDirector(rs.getString("director"));
        movie.setBasePrice(rs.getDouble("basePrice"));
        movie.setDuration(rs.getInt("duration"));
        return movie;
    }
}
