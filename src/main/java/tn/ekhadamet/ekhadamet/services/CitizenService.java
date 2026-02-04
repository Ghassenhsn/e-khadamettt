package tn.ekhadamet.ekhadamet.services;

import tn.ekhadamet.ekhadamet.Entities.Citizen;
import java.util.List;

public interface CitizenService {

    Citizen addCitizen(Citizen citizen);

    Citizen updateCitizen(Citizen citizen, Long id);

    void deleteCitizen(Long id);

    List<Citizen> addListCitizens(List<Citizen> citizens);

    // Variant without password check (since passwordHash should be encoded before calling service)
    Citizen addCitizenBasic(Citizen citizen);

    // If you still want a check variant (but better to do it in controller or dedicated registration service)
    String addCitizenWithPasswordCheck(Citizen citizen);

    List<Citizen> getAllCitizens();

    Citizen getCitizenById(Long id);

    Citizen getCitizenByCin(String cin);

    List<Citizen> getCitizensByEmailDomain(String email);  // e.g. pass "gmail.com"

    List<Citizen> getCitizensByCinStartingWith(String prefix);

    // Optional duplicates / variants from your original
    List<Citizen> getCitizens();           // alias for getAllCitizens
    void deleteCitizenById(Long id);       // alias
}