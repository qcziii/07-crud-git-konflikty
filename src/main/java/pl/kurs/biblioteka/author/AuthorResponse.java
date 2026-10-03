package pl.kurs.biblioteka.author;

import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookDto;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        List<BookDto> books
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBooks().stream()
                        .map(BookDto::from)
                        .toList()
        );
    }
}
