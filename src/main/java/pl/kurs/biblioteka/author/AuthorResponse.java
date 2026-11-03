package pl.kurs.biblioteka.author;

import pl.kurs.biblioteka.book.BookSummaryDto;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        List<BookSummaryDto> books
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBooks().stream().map(BookSummaryDto::from).toList()
        );
    }
}
