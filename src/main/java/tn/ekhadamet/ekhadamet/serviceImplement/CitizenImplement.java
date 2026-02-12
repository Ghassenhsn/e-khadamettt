// CitizenImplement (simplified, no duplicates)
package tn.ekhadamet.ekhadamet.serviceImplement;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.ekhadamet.ekhadamet.Entities.Citizen;
import tn.ekhadamet.ekhadamet.repository.CitizenRepository;
import tn.ekhadamet.ekhadamet.services.CitizenService;

import java.util.List;

@Service
public class CitizenImplement implements CitizenService {

    @Autowired
    private CitizenRepository citizenRepo;

    @Override
    public Citizen addCitizen(Citizen citizen) {
        return citizenRepo.save(citizen);
    }

    @Override
    public Citizen updateCitizen(Citizen citizen, Long id) {
        Citizen existing = getCitizenById(id);

        if (citizen.getFirstNameFr() != null) existing.setFirstNameFr(citizen.getFirstNameFr());
        if (citizen.getLastNameFr() != null) existing.setLastNameFr(citizen.getLastNameFr());
        if (citizen.getFirstNameAr() != null) existing.setFirstNameAr(citizen.getFirstNameAr());
        if (citizen.getLastNameAr() != null) existing.setLastNameAr(citizen.getLastNameAr());
        if (citizen.getPasswordHash() != null) existing.setPasswordHash(citizen.getPasswordHash());
        if (citizen.getRole() != null) existing.setRole(citizen.getRole());
        if (citizen.getPreferredLanguage() != null) existing.setPreferredLanguage(citizen.getPreferredLanguage());

        return citizenRepo.save(existing);
    }

    @Override
    public void deleteCitizen(Long id) {
        if (!citizenRepo.existsById(id)) {
            throw new EntityNotFoundException("Citizen not found with id: " + id);
        }
        citizenRepo.deleteById(id);
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
}