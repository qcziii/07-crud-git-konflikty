package pl.kurs.biblioteka.author;

import lombok.Builder;
import lombok.Value;
import pl.kurs.biblioteka.book.BookSummaryDto;

import java.util.List;

@Value
@Builder
public class AuthorDto {
    Long id;
    String name;
    List<BookSummaryDto> books;
}
