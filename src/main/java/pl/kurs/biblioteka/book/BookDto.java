package pl.kurs.biblioteka.book;

public record BookDto(
        Long id,
        String title,
        String isbn,
        int availableCopies) {

    public static BookDto from(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAvailableCopies());

    }

}
