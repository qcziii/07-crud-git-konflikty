package pl.kurs.biblioteka.book;

import lombok.Builder;

@Builder
public record BookDto(
        Long id,
        String title
) {
}
