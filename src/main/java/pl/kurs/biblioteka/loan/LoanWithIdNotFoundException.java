package pl.kurs.biblioteka.loan;

public class LoanWithIdNotFoundException extends RuntimeException {
    public LoanWithIdNotFoundException(Long id) {
        super("Loan with id: " + id + " not found!");
    }
}
