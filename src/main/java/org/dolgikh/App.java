package org.dolgikh;

import org.dolgikh.dao.BookDAO;
import org.dolgikh.dao.LoanDAO;
import org.dolgikh.dao.ReaderDAO;
import org.dolgikh.service.LibraryService;
import org.dolgikh.ui.ConsoleInterface;

public class App {
    public static void main( String[] args ) {
        BookDAO bookDao = new BookDAO();
        ReaderDAO readerDao = new ReaderDAO();
        LoanDAO loanDao = new LoanDAO();
        LibraryService service = new LibraryService(bookDao, readerDao, loanDao);

        new ConsoleInterface(service).start();
    }
}
