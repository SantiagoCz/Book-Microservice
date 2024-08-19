package santiagoczarny.books.services;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import santiagoczarny.books.classes.AuthorDto;
import santiagoczarny.books.clients.AuthorClient;
import santiagoczarny.books.entities.Book;
import santiagoczarny.books.repositories.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorClient authorClient;

    public Book createBook(Book book){
        return bookRepository.save(book);
    }

    public Book editBook(Book book){
        return bookRepository.save(book);
    }

    public List<Book> findAllBooks(){
        return bookRepository.findAll();
    }

    public Optional<Book> findBookById(Long id){
        return bookRepository.findById(id);
    }

    public boolean existsByIsbnNumber(String isbnNumber) {
        return bookRepository.existsByIsbnNumber(isbnNumber);
    }

    public Book buildBook(Book book){
        return Book.builder()
                .isbnNumber(book.getIsbnNumber())
                .description(book.getDescription())
                .publicationDate(book.getPublicationDate())
                .authorIds(book.getAuthorIds())
                .build();
    }

    // Method to assign an author to multiple books
    public void assignAuthorToBooks(Long authorId, List<Long> bookIds) {
        // Iterate over the IDs of the books to assign them to the author
        for (Long bookId : bookIds) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("The author with ID " + authorId + " does not exist."));

            // Verify if the book is already assigned to the author
            if (!book.getAuthorIds().contains(authorId)) {
                book.getAuthorIds().add(authorId);
                bookRepository.save(book); // Save the book with the newly assigned author
            }
        }
    }

    // Method to verify the existence of authors and assign their IDs
    public List<Long> verifyAndAssignAuthors(List<Long> authorIds) throws Exception {
        List<Long> authorsToAssign = new ArrayList<>();
        for (Long id : authorIds) {
            try {
                AuthorDto author = authorClient.getAuthorById(id);
                authorsToAssign.add(author.getId());
            } catch (FeignException.NotFound e) {
                throw new Exception("Author with ID " + id + " not found.");
            }
        }
        return authorsToAssign;
    }

}
