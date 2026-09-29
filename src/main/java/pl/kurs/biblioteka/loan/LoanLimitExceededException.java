package pl.kurs.biblioteka.loan;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class LoanLimitExceededException extends RuntimeException {

    public LoanLimitExceededException(String readerEmail, int limit) {
        super("Czytelnik " + readerEmail + " przekroczył limit " + limit + " wypożyczeń");
    }
}
