package pl.urban.taw_backend.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.urban.taw_backend.dto.CreateOrderDTO;
import pl.urban.taw_backend.dto.OrderDTO;
import pl.urban.taw_backend.dto.OrderMenuDTO;
import pl.urban.taw_backend.model.*;
import pl.urban.taw_backend.repository.MenuRepository;
import pl.urban.taw_backend.repository.OrderRepository;
import pl.urban.taw_backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
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
        Order orders = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return convertToOrderDTO(orders);
    }

    public List<OrderDTO> getUserOrders(String subject) {

        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public Order createOrder(CreateOrderDTO orderDTO) {
        try {
            Order order = new Order();
            order.setStatus(orderDTO.getStatus());
            order.setTotalPrice(orderDTO.getTotalPrice());
            order.setDeliveryTime(orderDTO.getDeliveryTime());
            order.setComment(orderDTO.getComment());
            order.setPaymentMethod(orderDTO.getPaymentMethod());
            order.setPaymentId(orderDTO.getPaymentId());

            User user = userRepository.findById(orderDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User with id " + orderDTO.getUserId() + " not found"));
            order.setUser(user);

            List<OrderMenu> orderMenus = new ArrayList<>();
            if (orderDTO.getOrderMenus() != null && !orderDTO.getOrderMenus().isEmpty()) {
                for (OrderMenuDTO orderMenuDTO : orderDTO.getOrderMenus()) {
                    OrderMenu orderMenu = new OrderMenu();
                    orderMenu.setQuantity(orderMenuDTO.getQuantity());
                    Menu menu = menuRepository.findById(orderMenuDTO.getMenuId())
                            .orElseThrow(() -> new IllegalArgumentException("Menu with id " + orderMenuDTO.getMenuId() + " not found"));
                    orderMenu.setMenu(menu);
                    orderMenu.setOrder(order);
                    orderMenus.add(orderMenu);
                }
            }
            order.setOrderMenus(orderMenus);
            return orderRepository.save(order);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create order", e);
        }

    }

    public OrderDTO convertToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setId(order.getId());
        orderDTO.setDeliveryTime(order.getDeliveryTime());
        orderDTO.setPaymentId(order.getPaymentId());
        orderDTO.setPaymentMethod(order.getPaymentMethod());
        orderDTO.setComment(order.getComment());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setOrderDate(order.getOrderDate());
        List<OrderMenuDTO> orderMenusDTO = order.getOrderMenus().stream()
                .map(this::convertToOrderMenuDTO)
                .collect(Collectors.toCollection(ArrayList::new));
        orderDTO.setOrderMenus(orderMenusDTO);

        return orderDTO;
    }

    public OrderMenuDTO convertToOrderMenuDTO(OrderMenu orderMenu) {
        OrderMenuDTO orderMenuDTO = new OrderMenuDTO();
        orderMenuDTO.setQuantity(orderMenu.getQuantity());
        orderMenuDTO.setMenuId(orderMenu.getMenu().getId());
        return orderMenuDTO;
    }
}
