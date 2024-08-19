package santiagoczarny.books.classes;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class AuthorDto {

    private Long id;
    private String idNumber;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthDate;
    @ElementCollection
    private List<Long> bookIds;

}
