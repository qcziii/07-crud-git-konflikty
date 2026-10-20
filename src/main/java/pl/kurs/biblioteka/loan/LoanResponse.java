package pl.kurs.biblioteka.loan;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record LoanResponse(
        Long id,
        Long bookId,
        String bookTitle,
        String readerEmail,
        LocalDate loanDate
) {
}
