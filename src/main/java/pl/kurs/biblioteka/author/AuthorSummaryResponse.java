package pl.kurs.biblioteka.author;

public record AuthorSummaryResponse(
        Long id,
        String name
) {
    public static AuthorSummaryResponse from(Author author) {
        return new AuthorSummaryResponse(
                author.getId(),
                author.getName()
        );
    }
}