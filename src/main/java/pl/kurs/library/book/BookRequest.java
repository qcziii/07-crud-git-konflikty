package pl.kurs.library.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        @NotBlank(message = "Title cannot be blank") String title,
        @NotBlank(message = "ISBN cannot be blank") String isbn,
        @Min(value = 0, message = "availableCopies must be >= 0") int availableCopies,
        @NotNull(message = "ID cannot be null") Long authorId

) {
}
