package tn.ekhadamet.ekhadamet.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.ekhadamet.ekhadamet.Entities.Citizen;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {



    Optional<Citizen> findByEmail(String email);

    Optional<Citizen> findByCin(String cin);
    List<Citizen> findByCinStartingWith(String prefix);
    @Query("""
        SELECT c FROM Citizen c
        WHERE SUBSTRING(c.email, LOCATE('@', c.email) + 1) = :domain
    """)
    List<Citizen> findByEmailDomain(@Param("domain") String domain);

    Optional<Citizen> findByPhone(String phone);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByCin(String cin);
}