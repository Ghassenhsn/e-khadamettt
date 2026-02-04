package tn.ekhadamet.ekhadamet.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "attestation_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttestationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_number", unique = true)
    private String requestNumber;

    @Column(name = "address_declared", columnDefinition = "TEXT", nullable = false)
    private String addressDeclared;

    @Column(name = "steg_bill_path", nullable = false)
    private String stegBillPath;
    @Column(name = "other_proof_path")
    private String otherProofPath;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;
    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "processed_by_id")
    private Citizen processedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}