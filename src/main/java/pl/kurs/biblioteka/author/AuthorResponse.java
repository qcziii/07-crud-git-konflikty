package pl.kurs.biblioteka.author;

import lombok.Builder;
import pl.kurs.biblioteka.book.BookDto;

import java.util.List;

@Builder
public record AuthorResponse(
        Long id,
        String name,
        List<BookDto> books
) {
}
