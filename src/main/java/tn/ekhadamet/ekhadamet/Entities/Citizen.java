package tn.ekhadamet.ekhadamet.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "citizen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name (French) is required")
    @Size(max = 100)
    @Column(name = "first_name_fr", nullable = false, length = 100)
    private String firstNameFr;

    @NotBlank(message = "Last name (French) is required")
    @Size(max = 100)
    @Column(name = "last_name_fr", nullable = false, length = 100)
    private String lastNameFr;

    @NotBlank(message = "First name (Arabic) is required")
    @Size(max = 100)
    @Column(name = "first_name_ar", nullable = true, length = 100)
    private String firstNameAr;

    @NotBlank(message = "Last name (Arabic) is required")
    @Size(max = 100)
    @Column(name = "last_name_ar", nullable = true, length = 100)
    private String lastNameAr;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    private boolean emailVerified = false;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;  // nullable → will be filled at registration

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = true)
    private Language preferredLanguage = Language.FR;

    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ───────────────────────────────────────────────
    // Password helpers (these were missing → main compilation error)
    // ───────────────────────────────────────────────

    public void setGeneratedPassword(String rawPassword, PasswordEncoder encoder) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Generated password cannot be empty");
        }
        this.passwordHash = encoder.encode(rawPassword);
    }

    public boolean checkPassword(String rawPassword, PasswordEncoder encoder) {
        if (this.passwordHash == null) {
            return false;
        }
        return encoder.matches(rawPassword, this.passwordHash);
    }

    public String getFullNameFr() {
        return firstNameFr + " " + lastNameFr;
    }

    public String getFullNameAr() {
        return firstNameAr + " " + lastNameAr;
    }
}