package pl.kurs.biblioteka.common;

public class LoanLimitExceededException extends BaseConflictException {

    public LoanLimitExceededException(String readerEmail, int limit) {
        super("Czytelnik " + readerEmail + " przekroczył limit " + limit + " wypożyczeń");
    }
}
