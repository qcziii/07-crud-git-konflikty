package pl.kurs.biblioteka.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        @NotBlank(message = "Tytuł nie może być pusty") String title,
        @NotBlank(message = "ISBN nie może być pusty") String isbn,
        @Min(value = 0, message = "Liczba egzemplarzy nie może być ujemna") int availableCopies,
        @NotNull(message = "Identyfikator autora jest wymagany") Long authorId
) {
}
