package pl.urban.taw_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.urban.taw_backend.dto.AddressDTO;
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

    @GetMapping("/get")
    public ResponseEntity<?> getUserAddress(@RequestHeader("Authorization") String token) {
        try {
            String subject = jwtToken.extractSubjectFromToken(token.substring(7));
            AddressDTO addressDTO = addressService.getAddress(subject);
            return ResponseEntity.ok(addressDTO);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }


    @PostMapping("/add")
    public ResponseEntity<Boolean> addAddress(@RequestHeader("Authorization") String token, @RequestBody Address address) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        return ResponseEntity.ok(addressService.addAddress(subject, address));
    }

    @PutMapping("/update")
    public ResponseEntity<Boolean> updateAddress(@RequestHeader("Authorization") String token, @RequestBody Address address) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        return ResponseEntity.ok(addressService.updateAddress(subject, address));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteAddress(@RequestHeader("Authorization") String token, @RequestParam Long addressId) {
        String subject = jwtToken.extractSubjectFromToken(token.substring(7));
        return ResponseEntity.ok(addressService.deleteAddress(subject, addressId));
    }
}
