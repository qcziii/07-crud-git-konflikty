package pl.kurs.biblioteka.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AuthorRequest(
        @NotBlank String name,
        @Positive Integer birthYear
) {
}
