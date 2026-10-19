package pl.kurs.biblioteka.loan;

import pl.kurs.biblioteka.book.BookShortView;

import java.time.LocalDate;

public record LoanResponse(
        BookShortView book,
        String readerEmail,
        LocalDate loanDate
) {
    public static LoanResponse from(Loan loan, BookShortView book){
        return new LoanResponse(
        book,
        loan.getReaderEmail(),
        loan.getLoanDate());
    }
}
