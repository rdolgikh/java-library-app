package org.dolgikh.ui;

import org.dolgikh.model.Book;
import org.dolgikh.model.Loan;
import org.dolgikh.service.LibraryService;

import java.util.List;
import java.util.Scanner;

public class ConsoleInterface {
    private final LibraryService service;
    private final Scanner scanner;

    public ConsoleInterface(LibraryService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            printMenu();
            int choice = readIntInput("> ");
            handleChoice(choice);
        }
    }

    private void printMenu() {
        System.out.println("\n=== Библиотечная система ===");
        System.out.println("1. Добавить книгу");
        System.out.println("2. Удалить книгу");
        System.out.println("3. Найти книги по автору");
        System.out.println("4. Найти книги по названию");
        System.out.println("5. Найти книги по году");
        System.out.println("6. Показать все книги");
        System.out.println("7. Выдать книгу");
        System.out.println("8. Вернуть книгу");
        System.out.println("9. Список выданных книг");
        System.out.println("10. Список доступных книг");
        System.out.println("0. Выход");
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> addBook();
            case 2 -> removeBook();
            case 3 -> searchBooksByAuthor();
            case 4 -> searchBooksByTitle();
            case 5 -> searchBooksByYear();
            case 6 -> showAllBooks();
            case 7 -> lendBook();
            case 8 -> returnBook();
            case 9 -> showBorrowedBooks();
            case 10 -> showAvailableBooks();
            case 0 -> System.exit(0);
            default -> System.out.println("Неверная команда");
        }
    }

    // Реализация только требуемых методов:

    private void addBook() {
        System.out.println("\nДобавление книги:");
        String title = readStringInput("Название: ");
        String author = readStringInput("Автор: ");
        int year = readIntInput("Год: ");
        int quantity = readIntInput("Количество: ");

        service.addBook(title, author, year, quantity);
        System.out.println("Книга добавлена!");
    }

    private void removeBook() {
        int id = readIntInput("ID книги для удаления: ");
        service.removeBook(id);
        System.out.println("Книга удалена!");
    }

    private void searchBooksByAuthor() {
        String author = readStringInput("Автор: ");
        List<Book> books = service.findBooksByAuthor(author);
        printBooks(books);
    }

    private void searchBooksByTitle() {
        String title = readStringInput("Название: ");
        List<Book> books = service.findBooksByTitle(title);
        printBooks(books);
    }

    private void searchBooksByYear() {
        int year = readIntInput("Год: ");
        List<Book> books = service.findBooksByYear(year);
        printBooks(books);
    }

    private void showAllBooks() {
        printBooks(service.getAllBooks());
    }

    private void lendBook() {
        int bookId = readIntInput("ID книги: ");
        int readerId = readIntInput("ID читателя: ");
        int days = readIntInput("На сколько дней: ");

        try {
            service.lendBook(bookId, readerId, days);
            System.out.println("Книга выдана!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void returnBook() {
        int bookId = readIntInput("ID возвращаемой книги: ");
        try {
            service.returnBook(bookId);
            System.out.println("Книга возвращена!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showBorrowedBooks() {
        System.out.println("\nВыданные книги:");
        List<Loan> loans = service.getBorrowedBooks();
        if (loans.isEmpty()) {
            System.out.println("Нет выданных книг");
            return;
        }

        for (Loan loan : loans) {
            System.out.printf(
                    "Книга: %s | Читатель: %s | Вернуть до: %s%n",
                    loan.getBook().getTitle(),
                    loan.getReader().getFullName(),
                    loan.getReturnDate()
            );
        }
    }

    private void showAvailableBooks() {
        System.out.println("\nДоступные книги:");
        printBooks(service.getAvailableBooks());
    }

    private void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("Книги не найдены");
            return;
        }

        for (Book book : books) {
            System.out.printf(
                    "ID: %d | %s | %s | %d | Доступно: %d/%d%n",
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getYear(),
                    book.getAvailableCount(),
                    book.getQuantity()
            );
        }
    }

    private String readStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число");
            }
        }
    }
}