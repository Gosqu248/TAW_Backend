package pl.urban.taw_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.urban.taw_backend.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
