package tn.ekhadamet.ekhadamet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "First name (French) is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstNameFr;

    @NotBlank(message = "Last name (French) is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastNameFr;

    // Arabic names are optional – will fallback to French if not provided
    @Size(max = 100, message = "First name (Arabic) must not exceed 100 characters")
    private String firstNameAr;

    @Size(max = 100, message = "Last name (Arabic) must not exceed 100 characters")
    private String lastNameAr;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\-\\s()]*$", message = "Invalid phone number format")
    private String phone;

    // You can add birthDate as LocalDate or String later
    // private LocalDate birthDate;

    @Pattern(regexp = "^(fr|ar)$", message = "Preferred language must be 'fr' or 'ar'")
    private String preferredLanguage = "fr"; // default
}