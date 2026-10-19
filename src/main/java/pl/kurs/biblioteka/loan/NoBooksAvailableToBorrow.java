package pl.kurs.biblioteka.loan;

public class NoBooksAvailableToBorrow extends RuntimeException {
    public NoBooksAvailableToBorrow(Long id, String title) {
        super("Book: " + title + " with id: " + id + " not available at this time");
    }
}
