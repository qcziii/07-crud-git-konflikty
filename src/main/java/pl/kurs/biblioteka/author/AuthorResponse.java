package pl.kurs.biblioteka.author;

import pl.kurs.biblioteka.book.BookSummaryResponse;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        List<BookSummaryResponse> books
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBooks().stream().map(BookSummaryResponse::from).toList()
        );
    }
}
