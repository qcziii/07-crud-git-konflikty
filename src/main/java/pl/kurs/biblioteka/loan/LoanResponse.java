package pl.kurs.biblioteka.loan;

import pl.kurs.biblioteka.book.Book;

import java.time.LocalDate;
import java.util.Map;

public record LoanResponse(
        Long id,
        Map<Long, String> book,
        String readerEmail,
        LocalDate loanDate
) {
    public static LoanResponse from(Loan loan) {
        Book bookToLoan = loan.getBook();
        return new LoanResponse(loan.getId(), Map.of(bookToLoan.getId(), bookToLoan.getTitle()), loan.getReaderEmail(), loan.getLoanDate());
    }
}
