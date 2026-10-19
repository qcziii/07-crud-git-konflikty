package pl.kurs.biblioteka.book;

import pl.kurs.biblioteka.author.AuthorShortView;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        AuthorShortView author
) {
    public static BookResponse from(Book book, AuthorShortView author) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies(),
                author);
    }
}
