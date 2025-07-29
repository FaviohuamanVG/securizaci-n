package pe.edu.vallegrande.vg_ms_user.infrastructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import pe.edu.vallegrande.vg_ms_user.domain.model.UserSede;

public interface UserSedeRepository extends MongoRepository<UserSede, String> {
    List<UserSede> findByStatus(String status);
    List<UserSede> findByStatusNot(String status);
}