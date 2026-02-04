package tn.ekhadamet.ekhadamet.Entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "citizen",
        indexes = {
                @Index(name = "idx_citizen_cin", columnList = "cin", unique = true),
                @Index(name = "idx_citizen_email", columnList = "email")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 8, max = 8)
    @Column(nullable = false, unique = true, length = 8)
    private String cin;

    @NotBlank
    @Column(name = "first_name_fr", nullable = false, length = 100)
    private String firstNameFr;

    @NotBlank
    @Column(name = "last_name_fr", nullable = false, length = 100)
    private String lastNameFr;

    @NotBlank
    @Column(name = "first_name_ar", nullable = false, length = 100)
    private String firstNameAr;

    @NotBlank
    @Column(name = "last_name_ar", nullable = false, length = 100)
    private String lastNameAr;

    @NotBlank
    @Column(name = "address_fr", columnDefinition = "TEXT", nullable = false)
    private String addressFr;

    @NotBlank
    @Column(name = "address_ar", columnDefinition = "TEXT", nullable = false)
    private String addressAr;

    @Size(max = 15)
    private String phone;

    @Email
    @Column(length = 150, unique = true)
    private String email;

    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false)
    private Language preferredLanguage;

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
}