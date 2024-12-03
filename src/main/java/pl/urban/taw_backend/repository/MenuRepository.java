package pl.urban.taw_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.urban.taw_backend.model.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
