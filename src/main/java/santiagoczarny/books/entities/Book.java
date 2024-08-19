package santiagoczarny.books.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "isbn_number", nullable = false, unique = true)
    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "ISBN number must be a valid ISBN-10 or ISBN-13 format.")
    private String isbnNumber;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "description")
    private String description;

    @ElementCollection
    private List<Long> authorIds;
}

