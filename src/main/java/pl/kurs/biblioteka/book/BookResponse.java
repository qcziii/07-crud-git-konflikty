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
}
