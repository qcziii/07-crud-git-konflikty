package pl.kurs.biblioteka.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kurs.biblioteka.author.Author;
import pl.kurs.biblioteka.author.AuthorResponse;
import pl.kurs.biblioteka.book.BookDto;

import java.util.List;

@Component
public class AuthorMapper {

    private BookMapper bookMapper;

    public AuthorMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public AuthorResponse fromAuthor(Author author) {
        List<BookDto> bookDtoList = author.getBooks()
                .stream()
                .map(bookMapper::toDto)
                .toList();

        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .books(bookDtoList)
                .build();
    }
}
