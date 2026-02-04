package tn.ekhadamet.ekhadamet.services;

public interface OtpService {

    /**
     * Generates a unique OTP for the citizen identified by phone.
     * Returns the OTP code (for testing) and saves it in DB.
     */
    String generateOtp(String phone);

    /**
     * Validates the OTP code for the given phone.
     * Returns true if valid and marks it as used.
     */
    boolean validateOtp(String phone, String code);

    /**
     * Optional: Cleanup expired OTPs (can be called by scheduler)
     */
    void cleanupExpiredOtps();
}