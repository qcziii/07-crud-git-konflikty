package pl.kurs.biblioteka.author;

import pl.kurs.biblioteka.book.Book;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record AuthorResponse(
        Long id,
        String name,
        Map<Long, String> books
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                //id + tytul
                author.getBooks().stream().collect(Collectors.toMap(Book::getId, Book::getTitle))
        );
    }
}
