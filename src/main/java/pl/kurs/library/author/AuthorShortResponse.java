package pl.kurs.library.author;

import pl.kurs.library.book.BookShortResponse;

import java.util.List;

public record AuthorShortResponse (
        Long id,
        String name,
        List<BookShortResponse> books
){

    public static AuthorShortResponse from(Author author) {
        return new AuthorShortResponse(
                author.getId(),
                author.getName(),
                author.getBooks()
                        .stream()
                        .map(BookShortResponse::from)
                        .toList()
        );
    }
}
