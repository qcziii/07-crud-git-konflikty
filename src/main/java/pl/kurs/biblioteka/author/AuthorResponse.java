package pl.kurs.biblioteka.author;

import pl.kurs.biblioteka.book.Book;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        List<Book> books
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBooks()
        );
    }
}
