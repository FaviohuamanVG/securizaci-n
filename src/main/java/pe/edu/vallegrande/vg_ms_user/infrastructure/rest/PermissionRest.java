package pe.edu.vallegrande.vg_ms_user.infrastructure.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.vg_ms_user.domain.model.Permission;
import pe.edu.vallegrande.vg_ms_user.application.service.PermissionService;

@RestController
@RequestMapping("/api/v1/permissions")
@CrossOrigin(origins = "http://localhost:3000")
public class PermissionRest {
    @Autowired
    private PermissionService permissionService;

    @PostMapping
    public Mono<Permission> createPermission(@RequestBody Permission permission) {
        return permissionService.createPermission(permission);
    }

    @GetMapping
    public Flux<Permission> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @GetMapping("/{viewId}")
    public Mono<Permission> getPermissionByViewId(@PathVariable String viewId) {
        return permissionService.getPermissionByViewId(viewId);
    }

    @PutMapping("/{id}")
    public Mono<Permission> updatePermission(@PathVariable String id, @RequestBody Permission permission) {
        return permissionService.updatePermission(id, permission);
    }
}