package pl.kurs.library.loan;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull Long bookId,
        @NotBlank @Email String readerEmail
) {
}
