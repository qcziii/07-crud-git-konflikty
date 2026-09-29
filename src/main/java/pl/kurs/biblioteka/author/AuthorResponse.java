package pl.kurs.biblioteka.author;

import pl.kurs.biblioteka.book.BookResponseDetails;

import java.util.List;

public record AuthorResponse(
        Long id,
        String name,
        List<BookResponseDetails> books
) {
    public static AuthorResponse fromAuthor(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBooks().stream().map(BookResponseDetails::from).toList()
        );
    }
}
