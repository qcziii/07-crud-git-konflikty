package pl.kurs.biblioteka.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import pl.kurs.biblioteka.author.Author;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String isbn;

    private int availableCopies;

    private Integer publicationYear;

    @ManyToOne(optional = false)
    private Author author;

    protected Book() {
    }

    public Book(String title, String isbn, int availableCopies, Integer publicationYear, Author author) {
        this.title = title;
        this.isbn = isbn;
        this.availableCopies = availableCopies;
        this.publicationYear = publicationYear;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public Author getAuthor() {
        return author;
    }
}
