package pl.kurs.library.author;

import pl.kurs.library.book.Book;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        Integer birthYear,
        List<Book> books
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBirthYear(),
                author.getBooks()
        );
    }
}
