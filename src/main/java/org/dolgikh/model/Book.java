package org.dolgikh.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private int year;
    private int totalQuantity;
    private int borrowedCount;

    public Book() {}

    public Book(int id, String title, String author, int year, int totalQuantity, int borrowedCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.totalQuantity = totalQuantity;
        this.borrowedCount = borrowedCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getBorrowedCount() {
        return borrowedCount;
    }

    public void setBorrowedCount(int borrowedCount) {
        this.borrowedCount = borrowedCount;
    }

    public int getAvailableCount() {
        return totalQuantity - borrowedCount;
    }

    public boolean isAvailable() {
        return getAvailableCount() > 0;
    }
}
