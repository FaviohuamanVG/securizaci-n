
package pe.edu.vallegrande.vg_ms_user.application.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.vg_ms_user.domain.model.UserSede;
import pe.edu.vallegrande.vg_ms_user.infrastructure.exception.ResourceNotFoundException;
import pe.edu.vallegrande.vg_ms_user.infrastructure.repository.UserSedeRepository;
import pe.edu.vallegrande.vg_ms_user.infrastructure.repository.UserRepository;
import pe.edu.vallegrande.vg_ms_user.infrastructure.client.HeadquarterClient;
import pe.edu.vallegrande.vg_ms_user.infrastructure.dto.HeadquarterResponseDTO;

import java.util.List;
import java.util.Optional;

@Service
public class UserSedeService {

    private final UserSedeRepository userSedeRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final HeadquarterClient headquarterClient;

    public UserSedeService(UserSedeRepository userSedeRepository, UserRepository userRepository, UserService userService, HeadquarterClient headquarterClient) {
        this.userSedeRepository = userSedeRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.headquarterClient = headquarterClient;
    }

    public List<UserSede> getAllUserSedes(String statusFilter) {
        if (statusFilter != null && !statusFilter.isEmpty()) {
            return userSedeRepository.findByStatus(statusFilter);
        } else {
            return userSedeRepository.findAll();
        }
    }

    public List<UserSede> getAllActiveUserSedes() {
        return userSedeRepository.findByStatus("Activo");
    }

    public UserSede getUserSedeById(String id) {
        Optional<UserSede> userSede = userSedeRepository.findById(id);
        if (userSede.isEmpty() || !"Activo".equals(userSede.get().getStatus())) {
            throw new ResourceNotFoundException("UserSede not found with id: " + id);
        }
        return userSede.get();
    }

    public UserSede createUserSede(UserSede userSede) {
        // Validar cada headquarter (sede) antes de proceder
        userSede.getDetails().forEach(detail -> {
            HeadquarterResponseDTO hq = headquarterClient.getHeadquarterById(detail.getSedeId())
                    .blockOptional() // Usamos blockOptional para manejar de forma síncrona, acorde al estilo del servicio
                    .orElseThrow(() -> new ResourceNotFoundException("Headquarter not found with id: " + detail.getSedeId()));

            if (!"A".equalsIgnoreCase(hq.getStatus())) {
                throw new IllegalStateException("Headquarter with id " + detail.getSedeId() + " is not active.");
            }
        });

        // Obtener el rol activo del usuario desde el UserService
        String role = userService.getActiveRoleByUserId(userSede.getUserId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found or not active with id: " + userSede.getUserId())))
                .block();

        // Asignar el rol a cada detalle si no se proporciona
        if (userSede.getDetails() != null) {
            for (UserSede.UserSedeDetail detail : userSede.getDetails()) {
                if (detail.getRole() == null) {
                    detail.setRole(role);
                }
            }
        }

        userSede.setStatus("Activo"); // Estado inicial por defecto
        return userSedeRepository.save(userSede);
    }


    public UserSede updateUserSede(String id, UserSede userSedeDetails) {
        // Buscar el UserSede existente
        UserSede existing = userSedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserSede not found with id: " + id));

        // Validar cada headquarter (sede) en los nuevos detalles
        userSedeDetails.getDetails().forEach(detail -> {
            HeadquarterResponseDTO hq = headquarterClient.getHeadquarterById(detail.getSedeId())
                    .blockOptional()
                    .orElseThrow(() -> new ResourceNotFoundException("Headquarter not found with id: " + detail.getSedeId()));

            if (!"A".equalsIgnoreCase(hq.getStatus())) {
                throw new IllegalStateException("Headquarter with id " + detail.getSedeId() + " is not active.");
            }
        });

        // Actualizar campos básicos
        existing.setUserId(userSedeDetails.getUserId());
        existing.setAssignmentReason(userSedeDetails.getAssignmentReason());
        existing.setObservations(userSedeDetails.getObservations());
        existing.setStatus(userSedeDetails.getStatus());

        // Obtener el rol activo del usuario desde el UserService
        String role = userService.getActiveRoleByUserId(userSedeDetails.getUserId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found or not active with id: " + userSedeDetails.getUserId())))
                .block();

        // Reemplazar detalles con roles asignados si es necesario
        List<UserSede.UserSedeDetail> newDetails = userSedeDetails.getDetails();
        if (newDetails != null) {
            for (UserSede.UserSedeDetail detail : newDetails) {
                if (detail.getRole() == null) {
                    detail.setRole(role);
                }
            }
            existing.setDetails(newDetails);
        }

        return userSedeRepository.save(existing);
    }

    public void deleteUserSede(String id) {
        UserSede userSede = userSedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserSede not found with id: " + id));
        userSede.setStatus("Inactivo"); // Logical deletion
        userSedeRepository.save(userSede);
    }

    public UserSede activateUserSede(String id) {
        UserSede userSede = userSedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserSede not found with id: " + id));
        userSede.setStatus("Activo"); // Activate
        return userSedeRepository.save(userSede);
    }
}
