package pl.kurs.biblioteka.author;

import lombok.Builder;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookResponse;

import java.util.List;

@Builder
public record AuthorResponse(
        Long id,
        String name,
        List<BookResponse> books
) {
    public static AuthorResponse from(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .books(author.getBooks().stream()
                        .map(BookResponse::from)
                        .toList())
                .build();
    }
}
