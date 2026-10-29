package pl.kurs.biblioteka.loan;

import pl.kurs.biblioteka.book.Book;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        BookSummary book,
        LocalDate loanDate
) {

    public record BookSummary(Long id, String title) {
        static BookSummary from(Book book) {
            return new BookSummary(book.getId(), book.getTitle());
        }
    }

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                BookSummary.from(loan.getBook()),
                loan.getLoanDate()
        );
    }
}
