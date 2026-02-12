// CitizenService interface (minimal & clean)
package tn.ekhadamet.ekhadamet.services;

import tn.ekhadamet.ekhadamet.Entities.Citizen;
import java.util.List;

public interface CitizenService {

    Citizen addCitizen(Citizen citizen);

    Citizen updateCitizen(Citizen citizen, Long id);

    void deleteCitizen(Long id);

    List<Citizen> getAllCitizens();

    Citizen getCitizenById(Long id);
}