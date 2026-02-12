package tn.ekhadamet.ekhadamet.services;

import tn.ekhadamet.ekhadamet.Entities.Language;

public interface OtpService {




    String sendOtp(String email, Language lang, String purpose);

    boolean verifyOtp(String email, String code, String purpose);

}