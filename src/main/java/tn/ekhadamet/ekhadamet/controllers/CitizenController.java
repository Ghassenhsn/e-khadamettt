package tn.ekhadamet.ekhadamet.controllers;

import tn.ekhadamet.ekhadamet.Entities.Citizen;
import tn.ekhadamet.ekhadamet.repository.CitizenRepository;
import tn.ekhadamet.ekhadamet.services.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/citizens")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4300"}, allowCredentials = "true")
public class CitizenController {

    @Autowired
    private CitizenRepository citizenRepo;

    @Autowired
    private CitizenService citizenService;

    @PostMapping("/register")
    public Citizen addCitizen(@RequestBody Citizen citizen) {
        return citizenService.addCitizen(citizen);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCitizen(@PathVariable("id") Long id) {
        citizenService.deleteCitizen(id);
    }

    @DeleteMapping("/delete")
    public void deleteCitizenByParam(@RequestParam("id") Long id) {
        citizenService.deleteCitizen(id);
    }

    @PostMapping("/saveall")
    public List<Citizen> addCitizens(@RequestBody List<Citizen> citizens) {
        return citizenService.addListCitizens(citizens);
    }

    @PostMapping("/addwithconfpassword")
    public String addCitizenWithConfPassword(@RequestBody Citizen citizen) {
        return citizenService.addCitizenWithPasswordCheck(citizen);
    }

    @PostMapping("/addwithcin")
    public String addCitizenWithCinCheck(@RequestBody Citizen citizen) {
        return citizenService.addCitizenWithPasswordCheck(citizen); // or implement CIN check
    }

    @PutMapping("/update/{id}")
    public Citizen updateCitizen(@PathVariable Long id, @RequestBody Citizen citizen) {
        return citizenService.updateCitizen(citizen, id);
    }

    @GetMapping("/all")
    public List<Citizen> getAllCitizens() {
        return citizenService.getAllCitizens();
    }

    @GetMapping("/findByCin/{cin}")
    public Citizen findByCin(@PathVariable String cin) {
        return citizenService.getCitizenByCin(cin);
    }

    @GetMapping("/getcitizenswt/{prefix}")
    public List<Citizen> getCitizensStartingWith(@PathVariable String prefix) {
        return citizenService.getCitizensByCinStartingWith(prefix);
    }

    @GetMapping("/getbyemaildomain")
    public List<Citizen> getCitizensByEmailDomain(@RequestParam("email") String email) {
        return citizenService.getCitizensByEmailDomain(email);
    }

    // Added missing endpoint for profile fetch
    @GetMapping("/{id}")
    public Citizen getCitizenById(@PathVariable Long id) {
        return citizenRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));
    }
}