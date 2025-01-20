package pl.urban.taw_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.taw_backend.dto.CreateOrderDTO;
import pl.urban.taw_backend.dto.OrderDTO;
import pl.urban.taw_backend.model.Order;
import pl.urban.taw_backend.security.JwtUtil;
import pl.urban.taw_backend.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtToken;

    public OrderController(OrderService orderService, JwtUtil jwtToken) {
        this.orderService = orderService;
        this.jwtToken = jwtToken;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/getUserOrders")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@RequestHeader("Authorization") String token) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        return ResponseEntity.ok(orderService.getUserOrders(subject));
    }

    @PostMapping("/createOrder")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
