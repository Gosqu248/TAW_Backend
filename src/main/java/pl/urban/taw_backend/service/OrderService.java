package pl.urban.taw_backend.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.urban.taw_backend.dto.OrderDTO;
import pl.urban.taw_backend.model.*;
import pl.urban.taw_backend.repository.AddressRepository;
import pl.urban.taw_backend.repository.MenuRepository;
import pl.urban.taw_backend.repository.OrderRepository;
import pl.urban.taw_backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private final MenuRepository menuRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, MenuRepository menuRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
    }


    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + id + " not found"));

        return convertToOrderDTO(order);
    }

     public List<OrderDTO> getUserOrders(String subject) {
         User user = userRepository.findByEmail(subject)
                 .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

            List<Order> orders = orderRepository.findByUserId(user.getId(), Sort.by(Sort.Direction.DESC, "orderDate"));
            return orders.stream()
                    .map(this::convertToOrderDTO)
                    .collect(Collectors.toList());
     }

     public Order createOrder(Order orderRequest) {
        try {
            Order order = new Order();

            order.setStatus(orderRequest.getStatus());
            order.setTotalPrice(orderRequest.getTotalPrice());
            order.setDeliveryTime(orderRequest.getDeliveryTime());
            order.setComment(orderRequest.getComment());
            order.setPaymentMethod(orderRequest.getPaymentMethod());
            order.setPaymentId(orderRequest.getPaymentId());

            User user = userRepository.findById(orderRequest.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User with id " + orderRequest.getUser().getId() + " not found"));
            order.setUser(user);


            List<OrderMenu> orderMenus = new ArrayList<>();
            if(orderRequest.getOrderMenus() != null && !orderRequest.getOrderMenus().isEmpty()) {
               for (OrderMenu requestOrderMenu : orderRequest.getOrderMenus()) {
                   OrderMenu orderMenu = new OrderMenu();

                   orderMenu.setQuantity(requestOrderMenu.getQuantity());
                   Menu menu = menuRepository.findById(requestOrderMenu.getMenu().getId())
                           .orElseThrow(() -> new IllegalArgumentException("Menu with id " + requestOrderMenu.getMenu().getId() + " not found"));

                   orderMenu.setMenu(menu);
                   orderMenu.setOrder(order);

                   orderMenus.add(orderMenu);
               }
            }

            order.setOrderMenus(orderMenus);
            return orderRepository.save(order);
        } catch (Exception e) {
            throw new IllegalArgumentException("Order creation failed: " + e.getMessage());
        }
     }

    public OrderDTO convertToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setId(order.getId());
        orderDTO.setDeliveryTime(order.getDeliveryTime());
        orderDTO.setOrderMenus(order.getOrderMenus());
        orderDTO.setPaymentId(order.getPaymentId());
        orderDTO.setPaymentMethod(order.getPaymentMethod());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setOrderDate(order.getOrderDate());

        return orderDTO;
    }
}
