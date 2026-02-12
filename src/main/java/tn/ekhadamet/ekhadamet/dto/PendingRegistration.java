package tn.ekhadamet.ekhadamet.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tn.ekhadamet.ekhadamet.Entities.Language;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Getter
@Setter
@Data
class PendingRegistration {

    private String firstNameFr;
    private String lastNameFr;
    private String firstNameAr;
    private String lastNameAr;

}