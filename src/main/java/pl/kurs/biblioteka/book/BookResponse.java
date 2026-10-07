package pl.kurs.biblioteka.book;

import pl.kurs.biblioteka.author.AuthorSummary;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        AuthorSummary author
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies(),
                AuthorSummary.from(book.getAuthor())
        );
    }
}
