package tn.ekhadamet.ekhadamet.services;

public interface SmsService {
    void sendOtp(String phone, String otpCode);
}