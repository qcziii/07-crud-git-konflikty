package pl.kurs.biblioteka.loan;

import pl.kurs.biblioteka.book.BookDTO;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        BookDTO bookDTO,
        String readerEmail,
        LocalDate loanDate
) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                BookDTO.from(loan.getBook()),
                loan.getReaderEmail(),
                loan.getLoanDate()
        );
    }
}
