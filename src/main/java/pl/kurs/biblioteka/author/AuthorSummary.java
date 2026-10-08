package pl.kurs.biblioteka.author;

public record AuthorSummary(Long id, String name) {

    public static AuthorSummary from(Author author) {
        return new AuthorSummary(author.getId(), author.getName());
    }
}
