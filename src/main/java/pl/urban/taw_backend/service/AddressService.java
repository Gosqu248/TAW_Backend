package pl.urban.taw_backend.service;

import org.springframework.stereotype.Service;
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

    public Address getAddress(String subject) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
        return user.getAddress();
    }

    public Address addAddress(String subject, Address address) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));
        address.setUser(user);
        return addressRepository.save(address);
    }

    public Address updateAddress(String subject, Long addressId, Address updatedAddress) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address with this id not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address with this id does not belong to this user");
        }
        address.setStreet(updatedAddress.getStreet());
        address.setHouseNumber(updatedAddress.getHouseNumber());
        address.setFloorNumber(updatedAddress.getFloorNumber());
        address.setAccessCode(updatedAddress.getAccessCode());
        address.setCity(updatedAddress.getCity());
        address.setZipCode(updatedAddress.getZipCode());
        return addressRepository.save(address);
    }

    public Address deleteAddress(String subject, Long addressId) {
        User user = userRepository.findByEmail(subject)
                .orElseThrow(() -> new IllegalArgumentException("User with this email not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address with this id not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Address with this id does not belong to this user");
        }

        address.setUser(null);
        return addressRepository.save(address);
    }


}
