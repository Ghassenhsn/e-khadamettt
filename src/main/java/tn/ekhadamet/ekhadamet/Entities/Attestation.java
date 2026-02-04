package tn.ekhadamet.ekhadamet.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attestation",
        indexes = {
                @Index(name = "idx_attestation_number", columnList = "attestation_number", unique = true),
                @Index(name = "idx_attestation_request_id", columnList = "request_id", unique = true)
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attestation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 8, max = 50) // adjust according to your format
    @Column(name = "attestation_number", nullable = false, unique = true, length = 50)
    private String attestationNumber;

    @NotBlank
    @Column(name = "pdf_path", nullable = false, columnDefinition = "TEXT")
    private String pdfPath;

    @NotBlank
    @Column(name = "qr_code_data", nullable = false, columnDefinition = "TEXT")
    private String qrCodeData;

    @PastOrPresent
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @FutureOrPresent // or @Future depending on business rule
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @NotNull
    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by_id", nullable = false)  // ← fixed & explicit
    private Citizen issuedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AttestationStatus status = AttestationStatus.VALID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private AttestationRequest request;

    // Optional: automatic timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.issuedAt  = LocalDateTime.now(); // if not set elsewhere
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum AttestationStatus {
        VALID,
        EXPIRED

    }
}