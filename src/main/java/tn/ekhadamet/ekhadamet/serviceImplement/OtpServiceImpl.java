package tn.ekhadamet.ekhadamet.serviceImplement;

import org.springframework.stereotype.Service;
import tn.ekhadamet.ekhadamet.Entities.Language;
import tn.ekhadamet.ekhadamet.services.OtpService;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService {

    private final Map<String, String> otpStore = new ConcurrentHashMap<>(); // temp – use Redis in prod
    private final EmailService emailService;

    public OtpServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public String sendOtp(String email, Language lang, String purpose) { // purpose = "registration" or "login"
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(email + ":" + purpose, otp);

        // In production: set TTL 10 min with Redis
        // redisTemplate.opsForValue().set(email + ":" + purpose, otp, 10, TimeUnit.MINUTES);

        emailService.sendOtpEmail(email, otp, lang);
        return "pending";
    }

    @Override
    public boolean verifyOtp(String email, String code, String purpose) {
        String key = email + ":" + purpose;
        String stored = otpStore.get(key);
        if (stored == null || !stored.equals(code)) {
            return false;
        }
        otpStore.remove(key); // single use
        return true;
    }

    // Optional: cleanup expired (cron or TTL in Redis)
}