package pe.edu.vallegrande.vg_ms_user.infrastructure.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.vg_ms_user.domain.model.User;
import pe.edu.vallegrande.vg_ms_user.application.service.UserService;
import java.util.Set;
import java.util.List; // Import List

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserRest {
    @Autowired
    private UserService userService;

    @PostMapping
    public Mono<User> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping("/batch") // New endpoint for batch creation
    public Flux<User> createUsersBatch(@RequestBody List<User> users) {
        return userService.createUsersBatch(users);
    }

    @GetMapping
    public Flux<User> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {

        if (role != null) {
            if ("active".equalsIgnoreCase(status)) {
                return userService.getActiveUsersByRole(role);
            }
            return userService.getUsersByRole(role);
        }

        return userService.getAllUsers();
    }

    @GetMapping("/teachers")
    public Flux<User> getTeachers(@RequestParam(required = false) String status) {
        if ("active".equalsIgnoreCase(status)) {
            return userService.getActiveTeachers();
        }
        return userService.getAllTeachers();
    }

    @GetMapping("/users/{id}/role")
    public Mono<String> getActiveRole(@PathVariable String id) {
        return userService.getActiveRoleByUserId(id);
    }

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable String id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @PatchMapping("/{id}/deactivate")
    public Mono<User> deactivateUser(@PathVariable String id) {
        return userService.deactivateUser(id);
    }

    @PatchMapping("/{id}/activate")
    public Mono<User> activateUser(@PathVariable String id) {
        return userService.activateUser(id);
    }

    @PostMapping("/{userId}/permissions/{permission}")
    public Mono<User> addPermissionToUser(@PathVariable String userId, @PathVariable String permission) {
        return userService.addPermissionToUser(userId, permission);
    }

    @PostMapping("/{userId}/permissions/batch")
    public Mono<User> addPermissionsToUser(@PathVariable String userId, @RequestBody Set<String> permissions) {
        return userService.addPermissionsToUser(userId, permissions);
    }

    @DeleteMapping("/{userId}/permissions/{permission}")
    public Mono<User> removePermissionFromUser(@PathVariable String userId, @PathVariable String permission) {
        return userService.removePermissionFromUser(userId, permission);
    }

    @PutMapping("/{userId}/permissions")
    public Mono<User> setUserPermissions(@PathVariable String userId, @RequestBody Set<String> permissions) {
        return userService.setUserPermissions(userId, permissions);
    }

    @PostMapping("/migrate-permissions")
    public Flux<User> migrateUsersWithDefaultPermissions() {
        return userService.migrateUsersWithDefaultPermissions();
    }
}