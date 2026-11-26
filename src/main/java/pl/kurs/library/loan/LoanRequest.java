package pl.kurs.library.loan;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull(message = "Book ID must be number")
        @Min(value = 1, message = "Book ID must be positive")
        Long bookId,
        @NotBlank(message = "email cannot be empty")
        @Email( message = "Wrong email format")
        String readerEmail
) {
}
