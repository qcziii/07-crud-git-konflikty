package pl.kurs.biblioteka.author;


import pl.kurs.biblioteka.book.Book;

public record AuthorBookDto(
        Long id,
        String name
) {

    public static AuthorBookDto from(Book book){
        return new AuthorBookDto(book.getId(), book.getTitle());
    }
}
