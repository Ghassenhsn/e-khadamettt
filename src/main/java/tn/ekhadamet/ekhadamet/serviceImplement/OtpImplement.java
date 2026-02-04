package tn.ekhadamet.ekhadamet.serviceImplement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.ekhadamet.ekhadamet.Entities.Citizen;
import tn.ekhadamet.ekhadamet.Entities.OtpCode;
import tn.ekhadamet.ekhadamet.repository.CitizenRepository;
import tn.ekhadamet.ekhadamet.repository.OtpCodeRepository;
import tn.ekhadamet.ekhadamet.services.OtpService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpImplement implements OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final CitizenRepository citizenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public String generateOtp(String phone) {
        Citizen citizen = citizenRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("No citizen found with phone: " + phone));

        // Invalidate old unused OTPs for this citizen
        List<OtpCode> oldOtps = otpCodeRepository.findByCitizenAndUsedFalse(citizen);
        oldOtps.forEach(otp -> {
            otp.setUsed(true);
            otpCodeRepository.save(otp);
        });

        String otpCode;
        int attempts = 0;
        final int maxAttempts = 30;

        do {
            // Generate 8-digit unique OTP
            otpCode = String.format("%08d", secureRandom.nextInt(100_000_000));
            attempts++;

            if (attempts > maxAttempts) {
                throw new RuntimeException("Failed to generate unique OTP after " + maxAttempts + " attempts");
            }

        } while (otpCodeRepository.existsByCode(otpCode));

        OtpCode otp = OtpCode.builder()
                .code(otpCode)
                .citizen(citizen)
                .expirationTime(LocalDateTime.now().plusMinutes(10)) // 10 minutes validity
                .used(false)
                .build();

        otpCodeRepository.save(otp);

        // In production: send via SMS here
        // smsService.sendOtp(phone, otpCode);
        System.out.println("[OTP DEBUG] Generated unique OTP for " + phone + ": " + otpCode);

        return otpCode; // Return only for testing – remove in production
    }

    @Override
    @Transactional
    public boolean validateOtp(String phone, String code) {
        Citizen citizen = citizenRepository.findByPhone(phone).orElse(null);
        if (citizen == null) {
            return false;
        }

        Optional<OtpCode> otpOpt = otpCodeRepository.findFirstByCitizenAndCodeAndUsedFalseAndExpirationTimeAfter(
                citizen,
                code,
                LocalDateTime.now()
        );

        if (otpOpt.isPresent()) {
            OtpCode otp = otpOpt.get();
            otp.setUsed(true);
            otpCodeRepository.save(otp);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        List<OtpCode> expired = otpCodeRepository.findByExpirationTimeBefore(now);
        otpCodeRepository.deleteAll(expired);
        System.out.println("Cleaned " + expired.size() + " expired OTPs");
    }
}