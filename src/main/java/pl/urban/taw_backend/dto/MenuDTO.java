package pl.urban.taw_backend.dto;

import lombok.Data;

@Data
public class MenuDTO {
    private Long id;
    private String name;
    private String category;
    private String ingredients;
    private double price;
    private byte[] image;
    private String imageUrl;
}
