package pl.kurs.biblioteka.book;

import pl.kurs.biblioteka.author.AuthorDTO;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        AuthorDTO author
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies(),
                AuthorDTO.from(book.getAuthor())
        );
    }
}
