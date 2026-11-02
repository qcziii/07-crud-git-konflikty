package pl.kurs.biblioteka.mapper;

import org.springframework.stereotype.Component;
import pl.kurs.biblioteka.book.Book;
import pl.kurs.biblioteka.book.BookDto;
import pl.kurs.biblioteka.book.BookResponse;



@Component
public class BookMapper {

    public BookResponse fromBook(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .availableCopies(book.getAvailableCopies())
                .authorId(book.getAuthor().getId())
                .authorName(book.getAuthor().getName())
                .build();
    }

    public BookDto toDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .build();
    }
}
