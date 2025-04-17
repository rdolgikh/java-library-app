package org.dolgikh.dao;

import org.dolgikh.model.Book;
import org.dolgikh.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, year, quantity, available) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getYear());
            stmt.setInt(4, book.getQuantity());
            stmt.setBoolean(5, book.isAvailable());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    book.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add book", e);
        }
    }

    public Book getBookById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get book by ID", e);
        }
        return null;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(mapRowToBook(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all books", e);
        }
        return books;
    }

    public void updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, year = ?, quantity = ?, available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getYear());
            stmt.setInt(4, book.getQuantity());
            stmt.setBoolean(5, book.isAvailable());
            stmt.setInt(6, book.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book", e);
        }
    }

    public void deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete book", e);
        }
    }

    private Book mapRowToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setYear(rs.getInt("year"));
        book.setQuantity(rs.getInt("quantity"));
        book.setAvailable(rs.getBoolean("available"));
        return book;
    }
}