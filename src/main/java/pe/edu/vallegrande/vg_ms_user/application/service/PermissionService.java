package pe.edu.vallegrande.vg_ms_user.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.vg_ms_user.domain.model.Permission;
import pe.edu.vallegrande.vg_ms_user.infrastructure.repository.PermissionRepository;

@Service
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    public Mono<Permission> createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public Flux<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Mono<Permission> getPermissionByViewId(String viewId) {
        return permissionRepository.findByViewId(viewId);
    }

    public Mono<Permission> updatePermission(String id, Permission permission) {
        return permissionRepository.findById(id)
            .flatMap(existingPermission -> {
                existingPermission.setPermissions(permission.getPermissions());
                return permissionRepository.save(existingPermission);
            });
    }
} 