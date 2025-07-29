package pe.edu.vallegrande.vg_ms_user.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.vg_ms_user.domain.model.Permission;

public interface PermissionRepository extends ReactiveMongoRepository<Permission, String> {
    Mono<Permission> findByViewId(String viewId);
} 