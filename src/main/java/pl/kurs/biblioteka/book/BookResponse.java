package pl.kurs.biblioteka.book;

import pl.kurs.biblioteka.author.Author;

import java.util.Map;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        Map<Long, String> author
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies(),
                //id + name
                Map.of(book.getId(), book.getAuthor().getName())
        );
    }
}
