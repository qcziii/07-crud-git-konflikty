package pl.kurs.biblioteka.book;

public class DuplicateOfIsbnExeption extends RuntimeException {
    public DuplicateOfIsbnExeption(String isbn) {
        super("Book with this isbn: " + isbn + " already exists!");
    }
}
