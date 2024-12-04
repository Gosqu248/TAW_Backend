package pl.urban.taw_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.urban.taw_backend.model.Menu;
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
}
