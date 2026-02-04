package tn.ekhadamet.ekhadamet.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class OtpVerifyRequest {
    private String phone;
    private String code;
}
