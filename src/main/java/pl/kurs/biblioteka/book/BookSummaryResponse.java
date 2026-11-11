package pl.kurs.biblioteka.book;

public record BookSummaryResponse(
        Long id,
        String title
) {
    public static BookSummaryResponse from(Book book) {
        return new BookSummaryResponse(
                book.getId(),
                book.getTitle()
        );
    }
}