package pl.kurs.biblioteka.book;

import pl.kurs.biblioteka.author.AuthorSummaryResponse;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        AuthorSummaryResponse author
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies(),
                AuthorSummaryResponse.from(book.getAuthor())
        );
    }
}
