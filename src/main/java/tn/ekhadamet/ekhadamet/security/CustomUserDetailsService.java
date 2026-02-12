package tn.ekhadamet.ekhadamet.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.ekhadamet.ekhadamet.Entities.Citizen;
import tn.ekhadamet.ekhadamet.repository.CitizenRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CitizenRepository citizenRepository;

    public CustomUserDetailsService(CitizenRepository citizenRepository) {
        this.citizenRepository = citizenRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Loading user by email: " + email);

        // Use case-insensitive lookup (very common for emails)
        Citizen citizen = citizenRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("No citizen found with email: " + email));

        if (!citizen.isEmailVerified()) {
            throw new UsernameNotFoundException("Email not verified yet: " + email);
        }

        if (!citizen.isActive()) {
            throw new UsernameNotFoundException("Account is inactive: " + email);
        }

        String roleName = citizen.getRole().getRoleName().name(); // e.g. CITIZEN
        String authority = "ROLE_" + roleName;                    // → ROLE_CITIZEN

        System.out.println("User loaded → email: " + email + ", authority: " + authority);

        return User.builder()
                .username(citizen.getEmail())               // ← now email (must match JWT subject)
                .password(citizen.getPasswordHash())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(authority)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}