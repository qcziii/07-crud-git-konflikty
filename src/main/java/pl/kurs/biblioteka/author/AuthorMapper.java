package pl.kurs.biblioteka.author;

import org.springframework.stereotype.Component;
import pl.kurs.biblioteka.book.BookSummaryDto;

@Component
public class AuthorMapper {
    public static AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getName(), author.getBooks().stream().map(book -> BookSummaryDto.builder().build()).toList());
    }
}
