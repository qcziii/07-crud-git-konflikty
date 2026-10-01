package pl.kurs.biblioteka.author;

import pl.kurs.biblioteka.book.Book;

import java.util.List;

public record AuthorDTO(Long id, String name, List<Long> bookIds) {
    public static AuthorDTO from(Author author) {
        return new AuthorDTO(
                author.getId(),
                author.getName(),
                author.getBooks().stream().map(Book::getId).toList()
        );
    }
}
