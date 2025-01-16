package pl.urban.taw_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.urban.taw_backend.dto.AddressDTO;
import pl.urban.taw_backend.model.Address;
import pl.urban.taw_backend.model.User;
import pl.urban.taw_backend.repository.AddressRepository;
import pl.urban.taw_backend.repository.UserRepository;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public AddressDTO getAddress(String subject) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
        Address address = user.getAddress();
        if (address == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found for user");
        }
        return convertToDTO(address);
    }


    public Boolean addAddress(String subject, Address address) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
        address.setUser(user);
        addressRepository.save(address);
        return true;
    }

    public Boolean updateAddress(String subject, Address updatedAddress) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

        Address address = user.getAddress();

        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address with this id does not belong to this user");
        }
        address.setStreet(updatedAddress.getStreet());
        address.setHouseNumber(updatedAddress.getHouseNumber());
        address.setFloorNumber(updatedAddress.getFloorNumber());
        address.setAccessCode(updatedAddress.getAccessCode());
        address.setCity(updatedAddress.getCity());
        address.setZipCode(updatedAddress.getZipCode());
        addressRepository.save(address);
        return true;
    }

    public Boolean deleteAddress(String subject, Long addressId) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address with this id not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address with this id does not belong to this user");
        }

        address.setUser(null);
        addressRepository.save(address);
        return true;
    }

    public AddressDTO convertToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());
        dto.setHouseNumber(address.getHouseNumber());
        dto.setCity(address.getCity());
        dto.setZipCode(address.getZipCode());
        dto.setFloorNumber(address.getFloorNumber());
        dto.setAccessCode(address.getAccessCode());

        return dto;
    }

}
