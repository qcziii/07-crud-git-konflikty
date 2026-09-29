package pl.kurs.biblioteka.author;

public record AuthorDetailsResponse(
        Long id,
        String name
) {
    public static AuthorDetailsResponse fromAuthor(Author author) {
        return new AuthorDetailsResponse(
                author.getId(),
                author.getName()
        );
    }
}
