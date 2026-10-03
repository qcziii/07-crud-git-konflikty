package pl.kurs.biblioteka.book;

import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorDto;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        AuthorDto author
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies(),
                AuthorDto.from(book.getAuthor())
        );
    }
}
