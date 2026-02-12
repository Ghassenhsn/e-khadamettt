package tn.ekhadamet.ekhadamet.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.ekhadamet.ekhadamet.Entities.Citizen;
import tn.ekhadamet.ekhadamet.Entities.Language;
import tn.ekhadamet.ekhadamet.Entities.Role;
import tn.ekhadamet.ekhadamet.Entities.RoleName;
import tn.ekhadamet.ekhadamet.dto.*;
import tn.ekhadamet.ekhadamet.repository.CitizenRepository;
import tn.ekhadamet.ekhadamet.repository.RoleRepository;
import tn.ekhadamet.ekhadamet.security.JwtUtil;
import tn.ekhadamet.ekhadamet.serviceImplement.EmailService;
import tn.ekhadamet.ekhadamet.services.OtpService;

import java.util.Map;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(
        origins = {"http://localhost:4200", "http://localhost:4300"},
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final CitizenRepository citizenRepo;
    private final RoleRepository roleRepo;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration attempt for email: {}", request.getEmail());

        String email = request.getEmail().trim().toLowerCase();

        if (citizenRepo.existsByEmail(email)) {
            return status(HttpStatus.CONFLICT).body(Map.of("error", "Email already registered"));
        }

        Language lang = "ar".equalsIgnoreCase(request.getPreferredLanguage()) ? Language.AR : Language.FR;

        Role citizenRole = (Role) roleRepo.findByRoleName(RoleName.CITIZEN)
                .orElseThrow(() -> new IllegalStateException("CITIZEN role not found in database"));

        // Generate random password (12 characters example)
        String rawPassword = RandomStringUtils.randomAlphanumeric(10).toUpperCase() +
                RandomStringUtils.random(2, "!@#$%^&*") +
                RandomStringUtils.randomNumeric(2);

        Citizen citizen = Citizen.builder()
                .firstNameFr(request.getFirstNameFr().trim())
                .lastNameFr(request.getLastNameFr().trim())
                .firstNameAr(StringUtils.defaultIfBlank(request.getFirstNameAr(), request.getFirstNameFr()).trim())
                .lastNameAr(StringUtils.defaultIfBlank(request.getLastNameAr(), request.getLastNameFr()).trim())
                .email(email)
                .preferredLanguage(lang)
                .role(citizenRole)
                .active(true)
                .build();

        citizen.setGeneratedPassword(rawPassword, passwordEncoder);
        citizenRepo.save(citizen);

        // Send email with temporary password
        String subject = lang == Language.FR
                ? "eKhadamet – Vos identifiants temporaires"
                : "eKhadamet – بيانات الدخول المؤقتة";

        String body = lang == Language.FR
                ? "Bonjour,\n\nVotre compte a été créé.\n\nMot de passe temporaire : " + rawPassword + "\n\n" +
                "Utilisez ce mot de passe avec le code OTP qui vous sera envoyé lors de la connexion.\n\n" +
                "Cordialement,\nÉquipe eKhadamet"
                : "مرحبا،\n\nتم إنشاء حسابك.\n\nكلمة المرور المؤقتة: " + rawPassword + "\n\n" +
                "استخدم كلمة المرور هذه مع رمز OTP الذي سيتم إرساله عند تسجيل الدخول.\n\n" +
                "تحياتنا،\nفريق eKhadamet";

        emailService.sendSimpleEmail(email, subject, body);

        return ok(Map.of(
                "message", lang == Language.FR
                        ? "Compte créé. Vérifiez votre email pour le mot de passe temporaire."
                        : "تم إنشاء الحساب. تحقق من بريدك الإلكتروني للحصول على كلمة المرور المؤقتة.",
                "email", email
        ));
    }

    // ───────────────────────────────────────────────
    // Login Step 1: Verify password → send OTP
    // ───────────────────────────────────────────────
    @PostMapping("/login/password")
    public ResponseEntity<?> loginWithPassword(@Valid @RequestBody PasswordLoginRequest request) {
        logger.info("Password login attempt for email: {}", request.getEmail());

        String email = request.getEmail().trim().toLowerCase();

        Citizen citizen = citizenRepo.findByEmail(email)
                .orElse(null);

        if (citizen == null) {
            return status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No account found with this email"));
        }

        // Check password
        if (!citizen.checkPassword(request.getPassword(), passwordEncoder)) {
            return status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Incorrect password"));
        }

        // Password is correct → send OTP
        otpService.sendOtp(email, citizen.getPreferredLanguage(), "login");

        String message = citizen.getPreferredLanguage() == Language.FR
                ? "Mot de passe correct. Un code de vérification a été envoyé à votre email."
                : "كلمة المرور صحيحة. تم إرسال رمز التحقق إلى بريدك الإلكتروني.";

        return ok(Map.of(
                "message", message,
                "email", email,
                "step", "otp_required"
        ));
    }

    // ───────────────────────────────────────────────
    // Login Step 2: Verify OTP → issue JWT token
    // ───────────────────────────────────────────────
    @PostMapping("/login/otp")
    @Transactional
    public ResponseEntity<?> loginWithOtp(@Valid @RequestBody OtpVerifyRequest request) {
        logger.info("OTP verification attempt for email: {}, code: {}", request.getEmail(), request.getCode());

        String email = request.getEmail().trim().toLowerCase();

        Citizen citizen = citizenRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        boolean isValid = otpService.verifyOtp(email, request.getCode(), "login");

        if (!isValid) {
            return status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired OTP code"));
        }

        // Mark email as verified if this is the first successful login
        if (!citizen.isEmailVerified()) {
            citizen.setEmailVerified(true);
            citizenRepo.save(citizen);
            logger.info("Email verified during first login for: {}", email);
        }

        // Generate JWT
        String token = jwtUtil.generateToken(
                citizen.getEmail(),
                citizen.getRole().getRoleName().name(),
                citizen.getId()
        );

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(citizen.getId());
        response.setUsername(citizen.getEmail());
        response.setRole(citizen.getRole().getRoleName().name());

        return ok(response);
    }}