package pl.urban.taw_backend.dto;

import lombok.Data;

@Data
public class AddressDTO {
    Long id;
    String street;
    String houseNumber;
    String city;
    String zipCode;
    String accessCode;
    String floorNumber;
}
