package pl.kurs.biblioteka.author;

public record AuthorDto(
        Long id,
        String name) {


    public static AuthorDto from(Author author) {
        return new AuthorDto(
                author.getId(),
                author.getName()
        );
    }
}
