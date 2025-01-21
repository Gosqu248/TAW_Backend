package pl.urban.taw_backend.dto;

import lombok.Data;
import pl.urban.taw_backend.enums.OrderStatus;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class OrderDTO {

    private Long id;
    private String deliveryTime;
    private List<OrderMenuDTO> orderMenus;
    private String paymentId;
    private String paymentMethod;
    private String comment;
    private OrderStatus status;
    private double totalPrice;
    private ZonedDateTime orderDate;

}
