package org.dolgikh.dao;

import org.dolgikh.model.Reader;
import org.dolgikh.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReaderDAO {

    public void addReader(Reader reader) {
        String sql = "INSERT INTO readers (full_name, email, phone) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, reader.getFullName());
            stmt.setString(2, reader.getEmail());
            stmt.setString(3, reader.getPhone());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    reader.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении читателя", e);
        }
    }

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

    public List<Reader> getAllReaders() {
        List<Reader> readers = new ArrayList<>();
        String sql = "SELECT * FROM readers";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                readers.add(mapRowToReader(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при загрузке читателей", e);
        }
        return readers;
    }

    public void updateReader(Reader reader) {
        String sql = "UPDATE readers SET full_name = ?, email = ?, phone = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reader.getFullName());
            stmt.setString(2, reader.getEmail());
            stmt.setString(3, reader.getPhone());
            stmt.setInt(4, reader.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении читателя", e);
        }
    }

    public void deleteReader(int id) {
        String sql = "DELETE FROM readers WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении читателя", e);
        }
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