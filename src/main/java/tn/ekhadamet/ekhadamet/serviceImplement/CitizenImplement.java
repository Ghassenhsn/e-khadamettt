package tn.ekhadamet.ekhadamet.serviceImplement;

import tn.ekhadamet.ekhadamet.Entities.Citizen;
import tn.ekhadamet.ekhadamet.repository.CitizenRepository;  // ← your repo
import tn.ekhadamet.ekhadamet.services.CitizenService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitizenImplement implements CitizenService {

    @Autowired
    private CitizenRepository citizenRepo;

    @Override
    public Citizen addCitizen(Citizen citizen) {
        // In real app: encode password here or in dedicated registration method
        // citizen.setPasswordHash(passwordEncoder.encode(citizen.getPasswordHash()));
        return citizenRepo.save(citizen);
    }

    @Override
    public Citizen updateCitizen(Citizen citizen, Long id) {
        Citizen c = getCitizenById(id);

        // Partial update - only set non-null fields
        if (citizen.getCin() != null) {
            c.setCin(citizen.getCin());
        }
        if (citizen.getFirstNameFr() != null) {
            c.setFirstNameFr(citizen.getFirstNameFr());
        }
        if (citizen.getLastNameFr() != null) {
            c.setLastNameFr(citizen.getLastNameFr());
        }
        if (citizen.getFirstNameAr() != null) {
            c.setFirstNameAr(citizen.getFirstNameAr());
        }
        if (citizen.getLastNameAr() != null) {
            c.setLastNameAr(citizen.getLastNameAr());
        }
        if (citizen.getAddressFr() != null) {
            c.setAddressFr(citizen.getAddressFr());
        }
        if (citizen.getAddressAr() != null) {
            c.setAddressAr(citizen.getAddressAr());
        }
        if (citizen.getPhone() != null) {
            c.setPhone(citizen.getPhone());
        }
        if (citizen.getEmail() != null) {
            c.setEmail(citizen.getEmail());
        }
        if (citizen.getPasswordHash() != null) {
            // In real app: re-encode if changed
            c.setPasswordHash(citizen.getPasswordHash());
        }
        if (citizen.getRole() != null) {
            c.setRole(citizen.getRole());
        }
        if (citizen.getPreferredLanguage() != null) {
            c.setPreferredLanguage(citizen.getPreferredLanguage());
        }

        return citizenRepo.save(c);
    }

    @Override
    public void deleteCitizen(Long id) {
        if (!citizenRepo.existsById(id)) {
            throw new EntityNotFoundException("Citizen not found with id: " + id);
        }
        citizenRepo.deleteById(id);
    }

    @Override
    public List<Citizen> addListCitizens(List<Citizen> citizens) {
        return citizenRepo.saveAll(citizens);
    }

    @Override
    public Citizen addCitizenBasic(Citizen citizen) {
        return citizenRepo.save(citizen);
    }

    @Override
    public String addCitizenWithPasswordCheck(Citizen citizen) {
        // If you keep confirmPassword as transient field in DTO/form, check here
        // But since Citizen entity doesn't have confirmPassword → this is usually done in controller/DTO validation
        // Keeping similar to your style:
        // if (citizen.getPasswordHash() == null /* or other check */) { ... }
        // For now simplified:
        citizenRepo.save(citizen);
        return "Added successfully";
    }

    @Override
    public List<Citizen> getAllCitizens() {
        return citizenRepo.findAll();
    }

    @Override
    public Citizen getCitizenById(Long id) {
        return citizenRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Citizen not found with id: " + id));
    }

    @Override
    public Citizen getCitizenByCin(String cin) {
        return citizenRepo.findByCin(cin)
                .orElseThrow(() -> new EntityNotFoundException("Citizen not found with CIN: " + cin));
    }

    @Override
    public List<Citizen> getCitizensByEmailDomain(String domain) {
        return citizenRepo.findByEmailDomain(domain);
    }

    @Override
    public List<Citizen> getCitizensByCinStartingWith(String prefix) {
        return citizenRepo.findByCinStartingWith(prefix);
    }

    // Aliases / duplicates from your original code
    @Override
    public List<Citizen> getCitizens() {
        return getAllCitizens();
    }

    @Override
    public void deleteCitizenById(Long id) {
        deleteCitizen(id);
    }
}