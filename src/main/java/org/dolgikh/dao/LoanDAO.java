package org.dolgikh.dao;

import org.dolgikh.model.Book;
import org.dolgikh.model.Loan;
import org.dolgikh.model.Reader;
import org.dolgikh.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {

    public void borrowBook(Loan loan) {
        String sql = "INSERT INTO loans (book_id, reader_id, loan_date, return_date, returned) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loan.getBook().getId());
            stmt.setInt(2, loan.getReader().getId());
            stmt.setDate(3, loan.getLoanDate());
            stmt.setDate(4, loan.getReturnDate());
            stmt.setBoolean(5, false); // Книга выдана (не возвращена)
            stmt.executeUpdate();

            updateBookAvailability(loan.getBook().getId(), false);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при выдаче книги", e);
        }
    }

    public void returnBook(int bookId) {
        String updateLoanSql = "UPDATE loans SET returned = true WHERE book_id = ? AND returned = false";
        String updateBookSql = "UPDATE books SET available = true WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement loanStmt = conn.prepareStatement(updateLoanSql);
                 PreparedStatement bookStmt = conn.prepareStatement(updateBookSql)) {

                loanStmt.setInt(1, bookId);
                loanStmt.executeUpdate();

                bookStmt.setInt(1, bookId);
                bookStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при возврате книги", e);
        }
    }

    public List<Loan> getActiveLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, b.title, r.full_name " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN readers r ON l.reader_id = r.id " +
                "WHERE l.returned = false";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                loans.add(mapRowToLoan(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при загрузке активных выдач", e);
        }
        return loans;
    }

    private void updateBookAvailability(int bookId, boolean available) throws SQLException {
        String sql = "UPDATE books SET available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, available);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }

    private Loan mapRowToLoan(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));

        Reader reader = new Reader();
        reader.setId(rs.getInt("reader_id"));
        reader.setFullName(rs.getString("full_name"));

        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setBook(book);
        loan.setReader(reader);
        loan.setLoanDate(rs.getDate("loan_date"));
        loan.setReturnDate(rs.getDate("return_date"));
        loan.setReturned(rs.getBoolean("returned"));

        return loan;
    }
}