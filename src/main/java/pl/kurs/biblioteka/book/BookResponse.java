package pl.kurs.biblioteka.book;

import pl.kurs.biblioteka.author.Author;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        Long authorId,
        String authorName
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies(),
                book.getAuthor().getId(),
                book.getAuthor().getName()
        );
    }
}
