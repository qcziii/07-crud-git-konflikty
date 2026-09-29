package pl.kurs.biblioteka.author;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(@NotBlank String name) {
}
