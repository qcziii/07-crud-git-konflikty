package pl.kurs.library.book;

import pl.kurs.library.author.Author;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        Author author
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies(),
                book.getAuthor()
        );
    }
}
