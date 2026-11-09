package pl.kurs.library.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AuthorRequest(
        @NotBlank String name,
        @Positive Integer birthYear
) {
}
