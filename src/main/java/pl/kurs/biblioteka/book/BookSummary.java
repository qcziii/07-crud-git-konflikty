package pl.kurs.biblioteka.book;

public record BookSummary(Long id, String title) {

    public static BookSummary from(Book book) {
        return new BookSummary(book.getId(), book.getTitle());
    }
}
