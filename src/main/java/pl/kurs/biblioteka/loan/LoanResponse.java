package pl.kurs.biblioteka.loan;


import java.time.LocalDate;

public record LoanResponse(
        Long id,
        Long bookId,
        String readerEmail,
        LocalDate loanDate
) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getReaderEmail(),
                loan.getLoanDate()
        );
    }
}
