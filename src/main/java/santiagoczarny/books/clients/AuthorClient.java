package santiagoczarny.books.clients;

import org.springframework.web.bind.annotation.*;
import org.springframework.cloud.openfeign.FeignClient;
import santiagoczarny.books.classes.AuthorDto;

import java.util.List;

@FeignClient(name = "authors", url = "http://localhost:8085/author")
public interface AuthorClient {

    @GetMapping("/get/{id}")
    AuthorDto getAuthorById(@PathVariable Long id);

    @PostMapping("/assign")
    void assignBookToAuthors(@RequestParam Long bookId, @RequestParam List<Long> authorIds);

}
