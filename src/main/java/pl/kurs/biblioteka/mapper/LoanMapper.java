package pl.kurs.biblioteka.mapper;

import org.springframework.stereotype.Component;
import pl.kurs.biblioteka.loan.Loan;
import pl.kurs.biblioteka.loan.LoanResponse;

@Component
public class LoanMapper {

    public LoanResponse fromLoan(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .bookId(loan.getBook().getId())
                .bookTitle(loan.getBook().getTitle())
                .readerEmail(loan.getReaderEmail())
                .loanDate(loan.getLoanDate())
                .build();
    }
}
