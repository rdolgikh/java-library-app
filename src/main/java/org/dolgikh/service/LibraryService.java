package org.dolgikh.service;

import org.dolgikh.dao.BookDAO;
import org.dolgikh.dao.LoanDAO;
import org.dolgikh.dao.ReaderDAO;
import org.dolgikh.model.Book;
import org.dolgikh.model.Loan;
import org.dolgikh.model.Reader;

import java.time.LocalDate;
import java.util.List;

public class LibraryService {
    private final BookDAO bookDao;
    private final ReaderDAO readerDao;
    private final LoanDAO loanDao;

    public LibraryService(BookDAO bookDao, ReaderDAO readerDao, LoanDAO loanDao) {
        this.bookDao = bookDao;
        this.readerDao = readerDao;
        this.loanDao = loanDao;
    }

    public void addBook(String title, String author, int year, int quantity) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setYear(year);
        book.setQuantity(quantity);
        book.setBorrowedCount(0); // Новая книга, никто не брал

        bookDao.addBook(book);
    }

    public void removeBook(int bookId) {
        bookDao.deleteBook(bookId);
    }

    public List<Book> findBooksByAuthor(String author) {
        return bookDao.findBooksByAuthor(author);
    }

    public List<Book> findBooksByTitle(String title) {
        return bookDao.findBooksByTitle(title);
    }

    public List<Book> findBooksByYear(int year) {
        return bookDao.findBooksByYear(year);
    }

    public List<Book> getAllBooks() {
        return bookDao.getAllBooks();
    }

    public void lendBook(int bookId, int readerId, int daysToReturn) {
        Book book = bookDao.getBookById(bookId);
        Reader reader = readerDao.getReaderById(readerId);

        if (book == null || !book.isAvailable()) {
            throw new IllegalStateException("Книга недоступна");
        }

        if (reader == null) {
            throw new IllegalStateException("Читатель не найден");
        }

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setReader(reader);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusDays(daysToReturn));

        loanDao.createLoan(loan);
        bookDao.updateBorrowedCount(bookId, book.getBorrowedCount() + 1);
    }

    public void returnBook(int bookId) {
        Book book = bookDao.getBookById(bookId);
        if (book == null) {
            throw new IllegalStateException("Книга не найдена");
        }

        loanDao.returnBook(bookId);
        bookDao.updateBorrowedCount(bookId, book.getBorrowedCount() - 1);
    }

    public List<Loan> getBorrowedBooks() {
        return loanDao.getActiveLoans();
    }

    public List<Book> getAvailableBooks() {
        return bookDao.getAvailableBooks();
    }
}