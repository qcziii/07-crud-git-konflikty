package pl.kurs.biblioteka.loan;

import pl.kurs.biblioteka.book.BookDto;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        BookDto book,
        String readerEmail,
        LocalDate loanDate) {

    public static LoanResponse from(Loan loan){
        return new LoanResponse(loan.getId(), BookDto.from(loan.getBook()), loan.getReaderEmail(), loan.getLoanDate());
    }
}
