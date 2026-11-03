package pl.kurs.biblioteka.book;

public record BookSummaryDto(Long id, String title) {

    public static BookSummaryDto from(Book book) {
        return new BookSummaryDto(book.getId(), book.getTitle());
    }
}