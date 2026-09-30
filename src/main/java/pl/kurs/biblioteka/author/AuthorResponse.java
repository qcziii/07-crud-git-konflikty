package pl.kurs.biblioteka.author;

import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookDTO;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        List<BookDTO> books
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBooks().stream().map(BookDTO::from).toList()
        );
    }
}
