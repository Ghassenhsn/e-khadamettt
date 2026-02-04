package tn.ekhadamet.ekhadamet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.ekhadamet.ekhadamet.Entities.OtpCode;
import tn.ekhadamet.ekhadamet.Entities.Citizen;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    boolean existsByCode(String code);

    List<OtpCode> findByCitizenAndUsedFalse(Citizen citizen);

    Optional<OtpCode> findFirstByCitizenAndCodeAndUsedFalseAndExpirationTimeAfter(
            Citizen citizen,
            String code,
            LocalDateTime currentTime
    );

    // Optional: cleanup old OTPs
    List<OtpCode> findByExpirationTimeBefore(LocalDateTime time);
}