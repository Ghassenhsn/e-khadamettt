package tn.ekhadamet.ekhadamet.serviceImplement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tn.ekhadamet.ekhadamet.Entities.Language;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Async  // ← good practice — non-blocking
    public void sendOtpEmail(String to, String otp, Language lang) {
        String subject = lang == Language.AR
                ? "رمز التحقق لـ E-Khadamet"
                : "E-Khadamet Verification Code";

        String text = lang == Language.AR
                ? "رمز التحقق الخاص بك هو: " + otp + "\nصالح لمدة 10 دقائق.\nلا تشاركه مع أحد."
                : "Your verification code is: " + otp + "\nValid for 10 minutes.\nDo not share it.";

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);

        mailSender.send(msg);
    }

    @Async
    public void sendWelcomeWithPassword(String to, String plainPassword, Language lang) {
        String subject = lang == Language.AR
                ? "مرحباً بك في E-Khadamet"
                : "Welcome to E-Khadamet";

        String text = lang == Language.AR
                ? "تم إنشاء حسابك بنجاح!\nكلمة المرور المؤقتة: " + plainPassword +
                "\nيرجى تغييرها فور تسجيل الدخول.\nتحقق من بريدك لتفعيل الحساب."
                : "Your account has been created!\nTemporary password: " + plainPassword +
                "\nPlease change it after first login.\nVerify your email to activate.";

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);

        mailSender.send(msg);
    }
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // In production: use proper logging + maybe retry mechanism or dead-letter queue
            // For now: at least log the failure
            System.err.println("Failed to send email to " + to + " | Subject: " + subject);
            e.printStackTrace();
            // Optionally rethrow as custom exception if you want caller to handle it
        }
    }

}