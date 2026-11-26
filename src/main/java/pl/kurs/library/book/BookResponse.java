package pl.kurs.library.book;


import pl.kurs.library.author.AuthorSummary;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        AuthorSummary authorSummary
    ) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies(),
                AuthorSummary.from(book.getAuthor())
        );
    }
}
