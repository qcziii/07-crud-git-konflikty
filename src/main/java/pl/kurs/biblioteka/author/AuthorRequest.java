package pl.kurs.biblioteka.author;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(@NotBlank(message = "Pole nie moze byc puste") String name) {
}
