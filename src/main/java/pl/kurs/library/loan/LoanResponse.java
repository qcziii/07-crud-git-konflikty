package pl.kurs.library.loan;

import pl.kurs.library.book.BookShortResponse;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        BookShortResponse book,
        String readerEmail,
        LocalDate loanDate
) {

    public static LoanResponse from(Loan loan){
        return new LoanResponse(
                loan.getId(),
                BookShortResponse.from(loan.getBook()),
                loan.getReaderEmail(),
                loan.getLoanDate()
        );
    }
}
