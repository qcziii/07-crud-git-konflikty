package pl.kurs.biblioteka.book;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    int deleteBookById(Long id);

    boolean existsByIsbn(String isbn);
}
