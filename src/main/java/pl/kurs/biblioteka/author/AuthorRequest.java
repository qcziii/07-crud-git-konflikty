package pl.kurs.biblioteka.author;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(@NotBlank(message = "Imię autora nie może być puste") String name) {
}
