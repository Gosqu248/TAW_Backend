package pl.urban.taw_backend.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TwoFactorVerificationRequest {
    private String email;
    private String code;
}
