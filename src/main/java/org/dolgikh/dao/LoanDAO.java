package org.dolgikh.dao;

import org.dolgikh.model.Book;
import org.dolgikh.model.Loan;
import org.dolgikh.model.Reader;
import org.dolgikh.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {

    public int createLoan(Loan loan) {
        String sql = "INSERT INTO loans (book_id, reader_id, loan_date, return_date, returned) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, loan.getBook().getId());
            stmt.setInt(2, loan.getReader().getId());
            stmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(4, Date.valueOf(loan.getReturnDate()));
            stmt.setBoolean(5, false);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    loan.setId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при выдаче книги", e);
        }
        throw new RuntimeException("Не удалось получить ID выдачи");
    }

    public void returnBook(int bookId, int readerId) {
        String sql = "UPDATE loans SET returned = true WHERE book_id = ? AND reader_id = ? AND returned = false";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            stmt.setInt(2, readerId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new IllegalStateException("Выдача не найдена или книга уже возвращена");
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

    private Loan mapRowToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));

        Book book = new Book();
        book.setId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        loan.setBook(book);

        Reader reader = new Reader();
        reader.setId(rs.getInt("reader_id"));
        reader.setFullName(rs.getString("full_name"));
        loan.setReader(reader);

        loan.setLoanDate(rs.getDate("loan_date").toLocalDate());
        loan.setReturnDate(rs.getDate("return_date").toLocalDate());
        loan.setReturned(rs.getBoolean("returned"));

        return loan;
    }
}