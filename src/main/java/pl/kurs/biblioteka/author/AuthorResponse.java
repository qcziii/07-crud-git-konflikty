package pl.kurs.biblioteka.author;

import pl.kurs.biblioteka.book.BookSummary;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        List<BookSummary> books
) {
    public static AuthorResponse from(Author author) {
        List<BookSummary> books = author.getBooks().stream()
                .map(BookSummary::from)
                .toList();
        return new AuthorResponse(author.getId(), author.getName(), books);
    }
}
