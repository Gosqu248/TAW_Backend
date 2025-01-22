package pl.urban.taw_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import pl.urban.taw_backend.dto.AdminOrderDTO;
import pl.urban.taw_backend.dto.CreateOrderDTO;
import pl.urban.taw_backend.dto.OrderDTO;
import pl.urban.taw_backend.enums.OrderStatus;
import pl.urban.taw_backend.model.Order;
import pl.urban.taw_backend.security.JwtUtil;
import pl.urban.taw_backend.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@RequestHeader("Authorization") String token,  @PathVariable Long id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@RequestHeader("Authorization") String token) {
        try {
            String subject = jwtUtil.extractSubjectFromToken(token.substring(7));
            List<OrderDTO> orders = orderService.getUserOrders(subject);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdminOrderDTO>> getAllOrders(@RequestHeader("Authorization") String token) {
        try {
            List<AdminOrderDTO> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/createOrder")
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody CreateOrderDTO order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Order created with id: " + createdOrder.getId());
            response.put("orderId", createdOrder.getId().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/changeStatus/{id}")
    public ResponseEntity<OrderStatus> changeOrderStatus(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            OrderStatus status = orderService.changeOrderStatus(id);
            return ResponseEntity.ok(status);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
