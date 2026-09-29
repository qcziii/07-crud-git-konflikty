package pl.kurs.biblioteka.book;

public record BookResponseDetails(
        Long id,
        String title,
        String isbn,
        int availableCopies
) {
    public static BookResponseDetails from(Book book) {
        return new BookResponseDetails(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies()
        );
    }
}
