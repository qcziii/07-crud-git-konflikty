package pl.kurs.biblioteka.loan;

import pl.kurs.biblioteka.book.BookResponseDetails;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        BookResponseDetails book,
        String readerEmail,
        LocalDate loanDate
) {
    public static LoanResponse fromLoan(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                BookResponseDetails.from(loan.getBook()),
                loan.getReaderEmail(),
                loan.getLoanDate()
        );
    }
}
