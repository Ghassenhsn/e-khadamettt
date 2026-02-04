package tn.ekhadamet.ekhadamet.controllers;

import tn.ekhadamet.ekhadamet.Entities.Language;
import tn.ekhadamet.ekhadamet.dto.LoginRequest;
import tn.ekhadamet.ekhadamet.dto.LoginResponse;
import tn.ekhadamet.ekhadamet.dto.OtpVerifyRequest;
import tn.ekhadamet.ekhadamet.dto.RegisterRequest;
import tn.ekhadamet.ekhadamet.Entities.Citizen;
import tn.ekhadamet.ekhadamet.Entities.Role;
import tn.ekhadamet.ekhadamet.Entities.RoleName;
import tn.ekhadamet.ekhadamet.repository.CitizenRepository;
import tn.ekhadamet.ekhadamet.repository.RoleRepository;
import tn.ekhadamet.ekhadamet.security.JwtUtil;
import tn.ekhadamet.ekhadamet.services.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300"}, allowCredentials = "true")
public class AuthController {

    @Autowired private CitizenRepository citizenRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private OtpService otpService;

    // ────────────────────────────────────────────────
    // OLD PASSWORD LOGIN – keep only if you still want both methods
    // Comment out or remove if you want OTP-only login
    // ────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getPhone() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("Phone and password required");
        }

        Optional<Citizen> citizenOpt = citizenRepo.findByPhone(request.getPhone().trim());

        if (citizenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid phone number or password");
        }

        Citizen citizen = citizenOpt.get();

        // If you want to keep password login for admins or fallback
        // if (!passwordEncoder.matches(request.getPassword(), citizen.getPasswordHash())) {
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        //             .body("Invalid phone number or password");
        // }

        // If OTP-only → you can remove the password check above completely

        String roleName = citizen.getRole().getRoleName().name();

        String token = jwtUtil.generateToken(citizen.getPhone(), roleName, citizen.getId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(citizen.getId());
        response.setUsername(citizen.getPhone());
        response.setRole(roleName);

        return ResponseEntity.ok(response);
    }

    // ────────────────────────────────────────────────
    // REGISTER – now without fixed password (OTP will be used for login)
    // ────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String phone = request.getPhone();
        if (phone == null || phone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Phone number is required");
        }
        phone = phone.trim();

        if (citizenRepo.existsByPhone(phone)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Phone number already registered");
        }

        if (request.getCin() == null || citizenRepo.existsByCin(request.getCin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("CIN is required or already registered");
        }

        if (request.getEmail() != null && citizenRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already in use");
        }

        Citizen citizen = new Citizen();
        citizen.setCin(request.getCin());
        citizen.setFirstNameFr(request.getFirstNameFr());
        citizen.setLastNameFr(request.getLastNameFr());
        citizen.setFirstNameAr(request.getFirstNameAr());
        citizen.setLastNameAr(request.getLastNameAr());
        citizen.setAddressFr(request.getAddressFr());
        citizen.setAddressAr(request.getAddressAr());
        citizen.setPhone(phone);
        citizen.setEmail(request.getEmail());
        // No passwordHash – OTP will be used instead
        // citizen.setPasswordHash(null);  // or leave null

        Role citizenRole = (Role) roleRepo.findByRoleName(RoleName.CITIZEN)
                .orElseThrow(() -> new RuntimeException("CITIZEN role not found in database"));

        citizen.setRole(citizenRole);

        Language preferredLang = Language.FR;
        if (request.getPreferredLanguage() != null && !request.getPreferredLanguage().trim().isEmpty()) {
            String langInput = request.getPreferredLanguage().trim().toUpperCase();
            try {
                preferredLang = Language.valueOf(langInput);
            } catch (IllegalArgumentException ignored) {
                // keep default FR
            }
        }
        citizen.setPreferredLanguage(preferredLang);

        Citizen saved = citizenRepo.save(citizen);

        // Optional: auto-generate first OTP after registration
        // otpService.generateOtp(phone);

        LoginResponse response = new LoginResponse();
        response.setUserId(saved.getId());
        response.setUsername(saved.getPhone());
        response.setRole("CITIZEN");
        // No token here – user must request OTP to login

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ────────────────────────────────────────────────
    // OTP REQUEST – already good
    // ────────────────────────────────────────────────
    @PostMapping("/otp/request")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        if (phone == null || phone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone required"));
        }

        phone = phone.trim();

        try {
            String otp = otpService.generateOtp(phone);
            return ResponseEntity.ok(Map.of(
                    "message", "OTP sent to your phone",
                    "debugOtp", otp  // REMOVE in production!
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ────────────────────────────────────────────────
    // OTP VERIFY & LOGIN – already good
    // ────────────────────────────────────────────────
    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOtpAndLogin(@RequestBody OtpVerifyRequest request) {
        String phone = request.getPhone();
        String code = request.getCode();

        if (phone == null || code == null || phone.trim().isEmpty() || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Phone and code are required");
        }

        boolean valid = otpService.validateOtp(phone.trim(), code.trim());

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired OTP");
        }

        Citizen citizen = citizenRepo.findByPhone(phone.trim())
                .orElseThrow(() -> new RuntimeException("Citizen not found after OTP validation"));

        String roleName = citizen.getRole().getRoleName().name();
        String token = jwtUtil.generateToken(citizen.getPhone(), roleName, citizen.getId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(citizen.getId());
        response.setUsername(citizen.getPhone());
        response.setRole(roleName);

        return ResponseEntity.ok(response);
    }

    // ────────────────────────────────────────────────
    // Debug endpoint – keep for testing
    // ────────────────────────────────────────────────
    @GetMapping("/debug/whoami")
    public Map<String, Object> whoAmI() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> map = new HashMap<>();
        if (auth == null) {
            map.put("status", "not authenticated");
            return map;
        }

        map.put("status", "authenticated");
        map.put("name", auth.getName());
        map.put("principal", auth.getPrincipal().toString());
        map.put("authorities", auth.getAuthorities().stream().map(Object::toString).toList());
        map.put("isAuthenticated", auth.isAuthenticated());
        return map;
    }
}