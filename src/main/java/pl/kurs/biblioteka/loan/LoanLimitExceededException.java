package pl.kurs.biblioteka.loan;

public class LoanLimitExceededException extends Exception {

    public LoanLimitExceededException(String readerEmail, int limit) {
        super("Czytelnik " + readerEmail + " przekroczył limit " + limit + " wypożyczeń");
    }
}
