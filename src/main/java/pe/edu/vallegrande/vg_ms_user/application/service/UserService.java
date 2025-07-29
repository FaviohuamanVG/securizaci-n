
package pe.edu.vallegrande.vg_ms_user.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.vg_ms_user.domain.model.User;
import pe.edu.vallegrande.vg_ms_user.infrastructure.repository.UserRepository;
import pe.edu.vallegrande.vg_ms_user.infrastructure.client.InstitutionClient;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    // Constantes de estado
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    // Constantes de roles
    public static final String ROLE_DIRECTOR = "DIRECTOR";
    public static final String ROLE_PROFESOR = "PROFESOR";
    public static final String ROLE_AUXILIAR = "AUXILIAR";

    // Constantes de permisos
    public static final String PERMISSION_CREATE = "CREAR";
    public static final String PERMISSION_EDIT = "EDITAR";
    public static final String PERMISSION_VIEW = "VER";
    public static final String PERMISSION_DELETE = "ELIMINAR";
    public static final String PERMISSION_RESTORE = "RESTAURAR";

    private final UserRepository userRepository;
    private final InstitutionClient institutionClient;

    @Autowired
    public UserService(UserRepository userRepository, InstitutionClient institutionClient) {
        this.userRepository = userRepository;
        this.institutionClient = institutionClient;
    }

    public Mono<User> createUser(User user) {
        return institutionClient.getInstitutionById(user.getInstitutionId())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Institution with ID " + user.getInstitutionId() + " not found.")))
            .flatMap(institution -> {
                if (!"A".equalsIgnoreCase(institution.getStatus())) {
                    return Mono.error(new IllegalStateException("Institution with ID " + user.getInstitutionId() + " is not active."));
                }
                user.setStatus(STATUS_ACTIVE);
                if (user.getPermissions() == null || user.getPermissions().isEmpty()) {
                    user.setPermissions(getDefaultPermissionsByRole(user.getRole()));
                }
                return userRepository.save(user);
            });
    }

    public Flux<User> createUsersBatch(List<User> users) {
        return Flux.fromIterable(users)
            .concatMap(user ->
                institutionClient.getInstitutionById(user.getInstitutionId())
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("Institution with ID " + user.getInstitutionId() + " not found for user: " + user.getUserName())))
                    .flatMap(institution -> {
                        if (!"A".equalsIgnoreCase(institution.getStatus())) {
                            return Mono.error(new IllegalStateException("Institution with ID " + user.getInstitutionId() + " is not active for user: " + user.getUserName()));
                        }
                        user.setStatus(STATUS_ACTIVE);
                        if (user.getPermissions() == null || user.getPermissions().isEmpty()) {
                            user.setPermissions(getDefaultPermissionsByRole(user.getRole()));
                        }
                        return Mono.just(user);
                    })
            )
            .collectList()
            .flatMapMany(userRepository::saveAll);
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Flux<User> getAllActiveUsers() {
        return userRepository.findByStatus(STATUS_ACTIVE);
    }

    public Flux<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    public Flux<User> getActiveUsersByRole(String role) {
        return userRepository.findByRoleAndStatus(role, STATUS_ACTIVE);
    }

    public Flux<User> getActiveTeachers() {
        return userRepository.findByRoleAndStatus(ROLE_PROFESOR, STATUS_ACTIVE);
    }

    public Flux<User> getAllTeachers() {
        return userRepository.findByRole(ROLE_PROFESOR);
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> updateUser(String id, User user) {
        return userRepository.findById(id)
            .flatMap(existingUser -> {
                Mono<Void> validation = user.getInstitutionId() != null
                    ? institutionClient.getInstitutionById(user.getInstitutionId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Update failed: Institution with ID " + user.getInstitutionId() + " not found.")))
                        .flatMap(institution -> !"A".equalsIgnoreCase(institution.getStatus())
                            ? Mono.error(new IllegalStateException("Update failed: Institution with ID " + user.getInstitutionId() + " is not active."))
                            : Mono.empty()
                        ).then()
                    : Mono.empty();

                return validation.then(Mono.defer(() -> {
                    // Actualiza solo los campos proporcionados, manteniendo los existentes si no vienen en el request.
                    existingUser.setFirstName(user.getFirstName() != null ? user.getFirstName() : existingUser.getFirstName());
                    existingUser.setLastName(user.getLastName() != null ? user.getLastName() : existingUser.getLastName());
                    existingUser.setEmail(user.getEmail() != null ? user.getEmail() : existingUser.getEmail());
                    existingUser.setPhone(user.getPhone() != null ? user.getPhone() : existingUser.getPhone());
                    existingUser.setUserName(user.getUserName() != null ? user.getUserName() : existingUser.getUserName());
                    existingUser.setDocumentType(user.getDocumentType() != null ? user.getDocumentType() : existingUser.getDocumentType());
                    existingUser.setDocumentNumber(user.getDocumentNumber() != null ? user.getDocumentNumber() : existingUser.getDocumentNumber());
                    existingUser.setRole(user.getRole() != null ? user.getRole() : existingUser.getRole());
                    existingUser.setStatus(user.getStatus() != null ? user.getStatus() : existingUser.getStatus());
                    existingUser.setInstitutionId(user.getInstitutionId() != null ? user.getInstitutionId() : existingUser.getInstitutionId());
                    existingUser.setPermissions(user.getPermissions() != null ? user.getPermissions() : existingUser.getPermissions());

                    // Lógica para actualizar la contraseña solo si se proporciona una nueva.
                    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                        // Aquí deberías encriptar la contraseña antes de guardarla.
                        // Por ejemplo: existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                        existingUser.setPassword(user.getPassword());
                    }
                    
                    return userRepository.save(existingUser);
                }));
            });
    }

    public Mono<User> deactivateUser(String id) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    user.setStatus(STATUS_INACTIVE);
                    return userRepository.save(user);
                });
    }

    public Mono<User> activateUser(String id) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    user.setStatus(STATUS_ACTIVE);
                    return userRepository.save(user);
                });
    }

    public Mono<String> getActiveRoleByUserId(String userId) {
        return userRepository.findById(userId)
                .filter(user -> "ACTIVE".equals(user.getStatus()))
                .map(user -> user.getRole() != null ? user.getRole() : "SIN_ROL");
    }

    public Mono<User> addPermissionToUser(String userId, String permission) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    Set<String> permissions = user.getPermissions();
                    if (permissions == null) {
                        permissions = new HashSet<>();
                    }
                    permissions.add(permission);
                    user.setPermissions(permissions);
                    return userRepository.save(user);
                });
    }

    public Mono<User> addPermissionsToUser(String userId, Set<String> newPermissions) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    Set<String> permissions = user.getPermissions();
                    if (permissions == null) {
                        permissions = new HashSet<>();
                    }
                    permissions.addAll(newPermissions);
                    user.setPermissions(permissions);
                    return userRepository.save(user);
                });
    }

    public Mono<User> removePermissionFromUser(String userId, String permission) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    Set<String> permissions = user.getPermissions();
                    if (permissions != null) {
                        permissions.remove(permission);
                        user.setPermissions(permissions);
                    }
                    return userRepository.save(user);
                });
    }

    public Mono<User> setUserPermissions(String userId, Set<String> permissions) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    user.setPermissions(permissions);
                    return userRepository.save(user);
                });
    }

    private Set<String> getDefaultPermissionsByRole(String role) {
        Set<String> permissions = new HashSet<>();

        switch (role) {
            case ROLE_DIRECTOR:
                permissions.add(PERMISSION_CREATE);
                permissions.add(PERMISSION_EDIT);
                permissions.add(PERMISSION_VIEW);
                permissions.add(PERMISSION_DELETE);
                permissions.add(PERMISSION_RESTORE);
                break;
            case ROLE_PROFESOR:
            case ROLE_AUXILIAR:
                permissions.add(PERMISSION_VIEW);
                permissions.add(PERMISSION_EDIT);
                break;
            default:
                permissions.add(PERMISSION_VIEW);
                break;
        }

        return permissions;
    }

    public Flux<User> migrateUsersWithDefaultPermissions() {
        return userRepository.findAll()
                .flatMap(user -> {
                    if (user.getPermissions() == null || user.getPermissions().isEmpty()) {
                        Set<String> defaultPermissions = getDefaultPermissionsByRole(user.getRole());
                        user.setPermissions(defaultPermissions);
                        return userRepository.save(user);
                    }
                    return Mono.just(user);
                });
    }
}
