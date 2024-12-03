package pl.urban.taw_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.urban.taw_backend.model.OrderMenu;

public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {
}
