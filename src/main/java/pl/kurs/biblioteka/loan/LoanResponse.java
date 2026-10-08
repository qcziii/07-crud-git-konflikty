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

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getReaderEmail(),
                loan.getLoanDate()
        );
    }
}
