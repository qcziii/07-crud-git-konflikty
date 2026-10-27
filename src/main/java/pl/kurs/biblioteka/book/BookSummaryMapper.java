package pl.kurs.biblioteka.book;

import org.springframework.stereotype.Component;

@Component
public class BookSummaryMapper {
    public BookSummaryDto toDto(Book book) {
        return new BookSummaryDto(book.getId(), book.getTitle());
    }
}
