package pl.kurs.biblioteka.book;

public record BookDTO(Long id,
                      String title,
                      String isbn,
                      int availableCopies,
                      Long authorId) {

    public static BookDTO from(Book book) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies(),
                book.getAuthor().getId()
        );
    }
}
