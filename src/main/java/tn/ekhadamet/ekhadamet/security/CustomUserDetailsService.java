package tn.ekhadamet.ekhadamet.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.ekhadamet.ekhadamet.Entities.Citizen;
import tn.ekhadamet.ekhadamet.repository.CitizenRepository;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CitizenRepository citizenRepository;

    public CustomUserDetailsService(CitizenRepository citizenRepository) {
        this.citizenRepository = citizenRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        System.out.println("Loading user by identifier: " + identifier);

        Citizen citizen = citizenRepository.findByPhone(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("No citizen found with phone: " + identifier));

        String roleName = citizen.getRole().getRoleName().name(); // e.g. "CITIZEN"
        String authority = "ROLE_" + roleName;                    // → "ROLE_CITIZEN"

        System.out.println("User loaded → authorities: " + authority);

        return User.builder()
                .username(citizen.getPhone())  // must match JWT subject
                .password(citizen.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority(authority)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}