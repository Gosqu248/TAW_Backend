package pl.urban.taw_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderMenuDTO {

    private Long menuId;
    private int quantity;
}
