package pl.kurs.library.author;

import pl.kurs.library.book.BookResponse;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        List<BookResponse> books
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBooks()
                        .stream()
                        .map(BookResponse::from)
                        .toList()
        );
    }
}
