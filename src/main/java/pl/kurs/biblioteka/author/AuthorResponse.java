package pl.kurs.biblioteka.author;

import pl.kurs.biblioteka.book.BookShortView;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        List<BookShortView> books
) {
    public static AuthorResponse from(Author author, List<BookShortView> books) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                books
        );
    }
}
