package org.dolgikh.dao;

import org.dolgikh.model.Reader;
import org.dolgikh.util.DatabaseConnector;

import java.sql.*;

public class ReaderDAO {

    public Reader getReaderById(int id) {
        String sql = "SELECT * FROM readers WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReader(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске читателя", e);
        }
        return null;
    }

    private Reader mapRowToReader(ResultSet rs) throws SQLException {
        Reader reader = new Reader();
        reader.setId(rs.getInt("id"));
        reader.setFullName(rs.getString("full_name"));
        reader.setEmail(rs.getString("email"));
        reader.setPhone(rs.getString("phone"));
        return reader;
    }
}