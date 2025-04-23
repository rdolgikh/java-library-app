package org.dolgikh.ui;

import org.dolgikh.model.Book;
import org.dolgikh.model.Loan;
import org.dolgikh.service.LibraryService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        try {
            System.out.println("\nДобавление новой книги:");
            String title = readStringInput("Название: ");
            String author = readStringInput("Автор: ");
            int year = readIntInput("Год издания: ");
            int totalQuantity = readIntInput("Количество экземпляров: ");

            int bookId = service.addBook(title, author, year, totalQuantity);
            System.out.println("Книга успешно добавлена с ID: " + bookId);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении книги: " + e.getMessage());
        }
    }

    private void removeBook() {
        int id = readIntInput("ID книги для удаления: ");
        try {
            service.removeBook(id);
            System.out.println("Книга удалена!");
        } catch (IllegalStateException e) {
            System.err.println("Ошибка удаления: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Системная ошибка: " + e.getMessage());
        }
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
        try {
            System.out.println("\nВыдача книги:");

            int bookId = readIntInput("ID книги: ");
            int readerId = readIntInput("ID читателя: ");
            int daysToReturn = readIntInput("На сколько дней выдать (3-365): ");

            int loanId = service.lendBook(bookId, readerId, daysToReturn);
            LocalDate returnDate = LocalDate.now().plusDays(daysToReturn);

            System.out.printf(
                    "Книга успешно выдана. ID выдачи: %d, вернуть до: %s%n",
                    loanId,
                    returnDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            );

        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка ввода: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("Ошибка выдачи: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Системная ошибка: " + e.getMessage());
            }
    }


    private void returnBook() {
        int bookId = readIntInput("ID возвращаемой книги: ");
        int readerId = readIntInput("ID читателя: ");
        try {
            service.returnBook(bookId, readerId);
            System.out.println("Книга возвращена!");
        } catch (IllegalStateException e) {
            System.err.println("Ошибка возврата: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Системная ошибка: " + e.getMessage());
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
                    book.getTotalQuantity()
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
                int value = Integer.parseInt(scanner.nextLine());
                if (value <= 0) {
                    System.err.println("Число должно быть положительным");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.err.println("Ошибка: введите целое число");
            }
        }
    }
}