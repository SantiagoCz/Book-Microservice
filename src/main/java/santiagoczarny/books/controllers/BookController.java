package santiagoczarny.books.controllers;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import santiagoczarny.books.classes.Validations;
import santiagoczarny.books.clients.AuthorClient;
import santiagoczarny.books.entities.Book;
import santiagoczarny.books.services.BookService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorClient authorClient;

    @GetMapping("/all")
    public List<Book> findAllBooks(){
        return bookService.findAllBooks();
    }

    @GetMapping("/get/{id}")
    public Book getBookById(@PathVariable Long id) {
        Optional<Book> bookOptional = bookService.findBookById(id);

        return bookOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with ID: " + id));
    }

    @Transactional
    @PostMapping("/save")
    public ResponseEntity<?> saveBook(@RequestBody @Valid Book request,
                                      BindingResult result){
        // Handle validation errors
        ResponseEntity<?> validationResponse = Validations.handleValidationErrors(result);
        if (validationResponse != null) {
            return validationResponse;
        }
        try {

            // Check if the ISBN already exists
            if (bookService.existsByIsbnNumber(request.getIsbnNumber())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ISBN number must be unique.");
            }

            // Build the book from the request
            Book book = bookService.buildBook(request);

            // Check the existence of each author and assign their IDs to the book
            List<Long> authorsToAssign = bookService.verifyAndAssignAuthors(request.getAuthorIds());

            // Assign the list of author IDs to the book
            book.setAuthorIds(authorsToAssign);

            // Save the book
            bookService.createBook(book);

            // Assign the book to the authors
            authorClient.assignBookToAuthors(book.getId(), book.getAuthorIds());

            // Return a successful response
            return ResponseEntity.status(HttpStatus.CREATED).body("Book saved successfully.");

        } catch (Exception e) {
            String message = "An internal server error occurred: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignAuthorToBooks(@RequestParam Long authorId, @RequestParam List<Long> booksIds) {
        try {
            // Call the service method to assign the authors to the books
            bookService.assignAuthorToBooks(authorId, booksIds);

            // Return a successful response
            return ResponseEntity.ok("The author has been successfully assigned to the books.");

        } catch (IllegalArgumentException e) {
            // Handle invalid argument errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Handle other internal server errors
            String message = "An internal server error occurred: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

    @Transactional
    @PostMapping("/assign-authors")
    public ResponseEntity<?> assignAuthorsToBook(@RequestParam Long bookId, @RequestBody List<Long> authorIds) {
        try {
            // Verify that the book exists
            Optional<Book> optionalBook = bookService.findBookById(bookId);
            if (!optionalBook.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with ID " + bookId + " not found.");
            }
            Book book = optionalBook.get();

            // Using the service method to verify and assign authors
            List<Long> authorsToAssign = bookService.verifyAndAssignAuthors(authorIds);

            // Assign the list of author IDs to the book
            book.setAuthorIds(authorsToAssign);

            // Edit the book
            bookService.editBook(book);

            // Assign the book to the authors
            authorClient.assignBookToAuthors(book.getId(), book.getAuthorIds());

            // Return a successful response
            return ResponseEntity.ok("Authors assigned to book successfully.");

        } catch (Exception e) {
            // Handle general exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    @Transactional
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editBook(@RequestBody @Valid Book request,
                                              @PathVariable Long id,
                                              BindingResult result) {
        try {
            // Handle validation errors
            ResponseEntity<?> validationResponse = Validations.handleValidationErrors(result);
            if (validationResponse != null) {
                return validationResponse;
            }

            // Check if the professional exists
            Optional<Book> optionalBook = bookService.findBookById(id);
            if (!optionalBook.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found with ID: " + id);
            }

            // Retrieve the existing book
            Book existingBook = optionalBook.get();

            // Update the fields
            existingBook = bookService.buildBook(request);

            // Save the updated book
            bookService.editBook(existingBook);

            // Return a success response
            return ResponseEntity.ok("Book updated successfully.");

        } catch (Exception e) {
            // Handle the exception and return an error response
            String message = "An internal server error occurred: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

}
