package tn.ekhadamet.ekhadamet.serviceImplement;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.ekhadamet.ekhadamet.services.SmsService;

@Service
@RequiredArgsConstructor
public class SmsImplement implements SmsService {

    @Value("${sms.to.api.key}")
    private String apiKey;

    @Value("${sms.to.sender.id}")
    private String senderId;

    @Value("${sms.otp.message.template:Your OTP code is {code}. Valid for 10 minutes. Do not share it.}")
    private String otpMessageTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendOtp(String phone, String otpCode) {
        // Normalize phone
        String normalizedPhone = normalizePhone(phone);

        // Build message
        String messageBody = otpMessageTemplate.replace("{code}", otpCode);

        // SMS.to payload (form-urlencoded)
        String payload = "api_key=" + apiKey +
                "&to=" + normalizedPhone +
                "&from=" + senderId +
                "&text=" + messageBody +
                "&bypass_optout=true";  // optional – skip opt-out checks

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        try {
            String response = restTemplate.postForObject(
                    "https://api.sms.to/sms/send",
                    entity,
                    String.class
            );
            System.out.println("SMS.to response: " + response);
        } catch (Exception e) {
            System.err.println("Failed to send SMS via SMS.to: " + e.getMessage());
            throw new RuntimeException("SMS sending failed", e);
        }
    }

    private String normalizePhone(String phone) {
        phone = phone.trim().replaceAll("\\s+", "");
        if (phone.startsWith("0")) {
            phone = "+216" + phone.substring(1);
        } else if (!phone.startsWith("+")) {
            phone = "+216" + phone;
        }
        return phone;
    }
}