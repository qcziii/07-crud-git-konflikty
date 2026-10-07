package pl.kurs.biblioteka.loan;

import pl.kurs.biblioteka.book.BookSummary;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        String readerEmail,
        LocalDate loanDate,
        BookSummary book
) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getReaderEmail(),
                loan.getLoanDate(),
                BookSummary.from(loan.getBook())
        );
    }
}
