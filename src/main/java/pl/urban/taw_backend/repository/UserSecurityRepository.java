package pl.urban.taw_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.urban.taw_backend.model.UserSecurity;

public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long> {

}
