package santiagoczarny.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import santiagoczarny.books.entities.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbnNumber(String isbnNumber);
}
