package pl.kurs.library.book;

public record BookShortResponse(
        Long id,
        String title,
        int availableCopies,
        String name
) {
    public static BookShortResponse from(Book book) {
        return new BookShortResponse(
                book.getId(),
                book.getTitle(),
                book.getAvailableCopies(),
                book.getAuthor().getName()
        );
    }
}
