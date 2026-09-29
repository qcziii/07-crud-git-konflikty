package pl.kurs.biblioteka.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        @NotBlank String title,
        @NotBlank String isbn,
        @Min(0) int availableCopies,
        @NotNull Long authorId
) {
}
