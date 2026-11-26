package pl.kurs.library.book;

import pl.kurs.library.author.AuthorSummary;

public record BookShortResponse(
        Long id,
        String title,
        int availableCopies,
        AuthorSummary author
) {
    public static BookShortResponse from(Book book) {
        return new BookShortResponse(
                book.getId(),
                book.getTitle(),
                book.getAvailableCopies(),
                AuthorSummary.from(book.getAuthor())
        );
    }
}
