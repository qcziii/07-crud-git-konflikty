package pl.kurs.biblioteka.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<BookShortView> findByAuthorId(Long authorId);

    boolean existsByIsbn(String isbn);

    Optional<BookShortView> findProjectedById(Long id);
}
