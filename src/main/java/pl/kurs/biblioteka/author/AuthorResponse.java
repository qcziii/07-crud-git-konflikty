package pl.kurs.biblioteka.author;

import pl.kurs.biblioteka.book.Book;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        List<BookSummary> books
) {

    public record BookSummary(Long id, String title) {
        static BookSummary from(Book book) {
            return new BookSummary(book.getId(), book.getTitle());
        }
    }

    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBooks().stream()
                        .map(BookSummary::from)
                        .toList()
        );
    }
}
