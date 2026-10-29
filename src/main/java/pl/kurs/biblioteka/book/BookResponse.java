package pl.kurs.biblioteka.book;

import pl.kurs.biblioteka.author.Author;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        AuthorSummary author
) {

    public record AuthorSummary(Long id, String name) {
        static AuthorSummary from(Author author) {
            return new AuthorSummary(author.getId(), author.getName());
        }
    }

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
