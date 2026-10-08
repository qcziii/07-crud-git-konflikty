package pl.kurs.biblioteka.book;

import lombok.Builder;

@Builder
public record BookResponse(
        Long id,
        String title,
        String isbn,
        int availableCopies,
        Long authorId,
        String authorName
) {
    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .availableCopies(book.getAvailableCopies())
                .authorId(book.getAuthor().getId())
                .authorName(book.getAuthor().getName())
                .build();
    }
}
