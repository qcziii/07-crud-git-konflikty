package pl.kurs.biblioteka.book;

public class DuplicateIsbnException extends RuntimeException {

    public DuplicateIsbnException(String isbn) {
        super("Książka o ISBN " + isbn + " już istnieje");
    }
}
