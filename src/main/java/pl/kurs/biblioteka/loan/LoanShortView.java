package pl.kurs.biblioteka.loan;

import pl.kurs.biblioteka.book.BookShortView;

import java.time.LocalDate;

public interface LoanShortView {
    Long id();
    BookShortView bookShortView();
    LocalDate loanDate();
    String readerEmail();

}
