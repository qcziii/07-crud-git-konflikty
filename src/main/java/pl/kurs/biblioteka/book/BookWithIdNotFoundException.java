package pl.kurs.biblioteka.book;

public class BookWithIdNotFoundException extends RuntimeException {
    public BookWithIdNotFoundException(Long id) {
        super("Book with id: " + id + " not found!");
    }
}
