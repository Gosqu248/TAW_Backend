package pl.urban.taw_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.urban.taw_backend.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{
}
