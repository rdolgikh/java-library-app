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

    public int addBook(String title, String author, int year, int totalQuantity) {
        validateBookFields(title, author, year, totalQuantity);

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setYear(year);
        book.setTotalQuantity(totalQuantity);
        book.setBorrowedCount(0);

        return bookDao.addBook(book);
    }

    private void validateBookFields(String title, String author, int year, int totalQuantity) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Название книги не может быть пустым");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Название книги слишком длинное (макс. 100 символов)");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Автор не может быть пустым");
        }
        if (author.length() > 50) {
            throw new IllegalArgumentException("Имя автора слишком длинное (макс. 50 символов)");
        }
        int currentYear = LocalDate.now().getYear();
        if (year <= 0 || year > currentYear) {
            throw new IllegalArgumentException("Год издания должен быть между 1 и " + currentYear);
        }
        if (totalQuantity <= 0) {
            throw new IllegalArgumentException("Количество экземпляров должно быть положительным");
        }
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

    public int lendBook(int bookId, int readerId, int daysToReturn) {
        Book book = bookDao.getBookById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Книга с ID " + bookId + " не найдена");
        }

        if (!book.isAvailable()) {
            throw new IllegalStateException("Книга '" + book.getTitle() + "' недоступна для выдачи");
        }

        Reader reader = readerDao.getReaderById(readerId);
        if (reader == null) {
            throw new IllegalArgumentException("Читатель с ID " + readerId + " не найден");
        }

        validateLoanPeriod(daysToReturn);

        LocalDate loanDate = LocalDate.now();
        LocalDate returnDate = loanDate.plusDays(daysToReturn);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setReader(reader);
        loan.setLoanDate(loanDate);
        loan.setReturnDate(returnDate);

        int loanId = loanDao.createLoan(loan);
        bookDao.updateBorrowedCount(bookId, book.getBorrowedCount() + 1);

        return loanId;
    }

    private void validateLoanPeriod(int days) {
        if (days < 3) {
            throw new IllegalArgumentException("Минимальный срок выдачи - 3 дня");
        }

        if (days > 365) {
            throw new IllegalArgumentException("Максимальный срок выдачи - 1 год (365 дней)");
        }
    }


    public void returnBook(int bookId, int readerId) {
        Book book = bookDao.getBookById(bookId);
        if (book == null) {
            throw new IllegalStateException("Книга не найдена");
        }

        Reader reader = readerDao.getReaderById(readerId);
        if (reader == null) {
            throw new IllegalStateException("Читатель не найден");
        }

        loanDao.returnBook(bookId, readerId);
        bookDao.updateBorrowedCount(bookId, book.getBorrowedCount() - 1);
    }

    public List<Loan> getBorrowedBooks() {
        return loanDao.getActiveLoans();
    }

    public List<Book> getAvailableBooks() {
        return bookDao.getAvailableBooks();
    }
}