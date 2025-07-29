package pe.edu.vallegrande.vg_ms_user.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import pe.edu.vallegrande.vg_ms_user.domain.model.User;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Flux<User> findByRole(String role);
    Flux<User> findByRoleAndStatus(String role, String status);
    Flux<User> findByStatus(String status);
} 