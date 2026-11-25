package pl.kurs.library.author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {


   // @Override
   // @EntityGraph(attributePaths = "books")
   // List<Author> findAll();

    @Override
    @Query(value = """
        SELECT DISTINCT a FROM Author a
                LEFT JOIN FETCH a.books
        """)
    List<Author> findAll();

}
