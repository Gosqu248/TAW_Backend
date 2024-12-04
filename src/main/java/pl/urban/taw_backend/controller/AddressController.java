package pl.urban.taw_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.urban.taw_backend.model.Address;
import pl.urban.taw_backend.security.JwtUtil;
import pl.urban.taw_backend.service.AddressService;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;
    private final JwtUtil jwtToken;


    public AddressController(AddressService addressService, JwtUtil jwtToken) {
        this.addressService = addressService;
        this.jwtToken = jwtToken;
    }

    @GetMapping("/user")
    public ResponseEntity<Address> getUserAddress(@RequestHeader("Authorization") String token) {
        try {
            String subject = jwtToken.extractSubjectFromToken(token.substring(7));
            return ResponseEntity.ok(addressService.getAddress(subject));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Address> addAddress(@RequestHeader("Authorization") String token, @RequestBody Address address) {
        try {
            String subject = jwtToken.extractSubjectFromToken(token.substring(7));
            return ResponseEntity.ok(addressService.addAddress(subject, address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Address> updateAddress(@RequestHeader("Authorization") String token,@RequestParam Long addressId, @RequestBody Address address) {
        try {
            String subject = jwtToken.extractSubjectFromToken(token.substring(7));
            return ResponseEntity.ok(addressService.updateAddress(subject, addressId, address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Address> deleteAddress(@RequestHeader("Authorization") String token, @RequestParam Long addressId) {
        try {
            String subject = jwtToken.extractSubjectFromToken(token.substring(7));
            return ResponseEntity.ok(addressService.deleteAddress(subject, addressId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
