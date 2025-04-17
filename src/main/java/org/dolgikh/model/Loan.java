package org.dolgikh.model;

import java.sql.Date;

public class Loan {
    private int id;
    private Book book;
    private Reader reader;
    private Date loanDate;
    private Date returnDate;
    private boolean returned;

    public Loan() {}

    public Loan(int id, Book book, Reader reader, Date loanDate, Date returnDate) {
        this.id = id;
        this.book = book;
        this.reader = reader;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
