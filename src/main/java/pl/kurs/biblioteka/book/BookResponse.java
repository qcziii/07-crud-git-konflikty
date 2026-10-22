package pl.kurs.biblioteka.book;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        AuthorOfBookDto author
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies(),
                AuthorOfBookDto.from(book.getAuthor())
        );
    }
}
