package pl.urban.taw_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.taw_backend.model.Order;
import pl.urban.taw_backend.service.PayUService;

import java.util.Map;

@RestController
@RequestMapping("/api/payU")
public class PayUController {

    private final PayUService payUService;

    public PayUController(PayUService payUService) {
        this.payUService = payUService;
    }
    @PostMapping("/createPayment")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody Order order) {
            return ResponseEntity.ok(payUService.createOrder(order));
    }

    @GetMapping("/getPaymentStatus")
    public ResponseEntity<String> getPayUOrderStatus(@RequestParam String orderId) {
        try {
            String status = payUService.getOrderStatus(orderId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
