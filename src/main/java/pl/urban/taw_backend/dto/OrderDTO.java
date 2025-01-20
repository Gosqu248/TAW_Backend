package pl.urban.taw_backend.dto;

import lombok.Getter;
import lombok.Setter;
import pl.urban.taw_backend.enums.OrderStatus;
import pl.urban.taw_backend.model.OrderMenu;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDTO {

    private Long id;
    private String deliveryTime;
    private List<OrderMenu> orderMenus;
    private String paymentId;
    private String paymentMethod;
    private OrderStatus status;
    private double totalPrice;
    private ZonedDateTime orderDate;

}
