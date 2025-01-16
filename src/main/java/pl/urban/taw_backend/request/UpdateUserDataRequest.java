package pl.urban.taw_backend.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDataRequest {

    private String name;
    private String phoneNumber;
}
