package pl.kurs.biblioteka.book;

import pl.kurs.biblioteka.author.Author;

public record AuthorOfBookDto(
        Long id,
        String name
)
{
    public static AuthorOfBookDto from(Author author){
        return new AuthorOfBookDto(author.getId(), author.getName());
    }
}
