package tn.ekhadamet.ekhadamet.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RegisterRequest {

    // Required / unique fields
    private String cin;                 // National ID (8 digits in Tunisia)

    private String phone;               // Phone number (e.g. +216xxxxxxxx or 8 digits)

    private String password;            // Plain password (will be encoded in controller)

    // Personal info (bilingual)
    private String firstNameFr;
    private String lastNameFr;

    private String firstNameAr;
    private String lastNameAr;

    private String addressFr;
    private String addressAr;

    // Optional / secondary
    private String email;               // Can be null or checked for uniqueness

    // Language preference (sent as string from frontend: "FR", "AR", etc.)
    private String preferredLanguage;   // Will be converted to Language enum in controller

    // Optional: you can add more fields later if needed
    // private String nationality;      // e.g. "Tunisian"
    // private LocalDate birthDate;
    // private String gender;
}