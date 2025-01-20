package pl.urban.taw_backend.dto;

import lombok.Getter;
import lombok.Setter;
import pl.urban.taw_backend.enums.OrderStatus;
import java.util.List;

@Setter
@Getter
public class CreateOrderDTO {
    private OrderStatus status;
    private double totalPrice;
    private String deliveryTime;
    private String comment;
    private String paymentId;
    private String paymentMethod;
    private List<OrderMenuDTO> orderMenus;
    private Long userId;

}
