package pl.kurs.biblioteka.book;

public class BookWithIsbnNotFoundException extends RuntimeException {
    public BookWithIsbnNotFoundException(String isbn) {
        super("Book with isbn: " + isbn + " not found!");
    }
}
