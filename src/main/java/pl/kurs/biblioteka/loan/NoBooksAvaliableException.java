package pl.kurs.biblioteka.loan;

public class NoBooksAvaliableException extends RuntimeException {

    public NoBooksAvaliableException(String title) {
        super("No books: " + title + " avaliable");
    }
}
