package pl.kurs.library.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    @Override
    @Query(value = """
                    SELECT b from Book b
                    JOIN FETCH b.author
            """)
    List<Book> findAll();
}
