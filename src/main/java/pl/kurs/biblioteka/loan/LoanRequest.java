package pl.kurs.biblioteka.loan;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull(message = "Identyfikator książki jest wymagany") Long bookId,
        @NotBlank(message = "Email czytelnika jest wymagany")
        @Email(message = "Email czytelnika jest niepoprawny") String readerEmail
) {
}
