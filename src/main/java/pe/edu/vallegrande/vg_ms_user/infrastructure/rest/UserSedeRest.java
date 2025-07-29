package pe.edu.vallegrande.vg_ms_user.infrastructure.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vg_ms_user.application.service.UserSedeService;
import pe.edu.vallegrande.vg_ms_user.domain.model.UserSede;
import pe.edu.vallegrande.vg_ms_user.infrastructure.exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-sedes")
@CrossOrigin(origins = "*")
public class UserSedeRest {

    private final UserSedeService userSedeService;

    public UserSedeRest(UserSedeService userSedeService) {
        this.userSedeService = userSedeService;
    }

    @GetMapping
    public ResponseEntity<List<UserSede>> getAllUserSedes(@RequestParam(required = false) String status) {
        List<UserSede> userSedes = userSedeService.getAllUserSedes(status);
        return ResponseEntity.ok(userSedes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSede> getUserSedeById(@PathVariable String id) {
        try {
            UserSede userSede = userSedeService.getUserSedeById(id);
            return ResponseEntity.ok(userSede);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<UserSede> createUserSede(@RequestBody UserSede userSede) {
        UserSede createdUserSede = userSedeService.createUserSede(userSede);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserSede);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserSede> updateUserSede(@PathVariable String id, @RequestBody UserSede newUserSede) {
        try {
            UserSede updatedUserSede = userSedeService.updateUserSede(id, newUserSede);
            return ResponseEntity.ok(updatedUserSede);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUserSede(@PathVariable String id) {
        try {
            userSedeService.deleteUserSede(id); // This now performs logical deletion
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserSede> activateUserSede(@PathVariable String id) {
        try {
            UserSede activatedUserSede = userSedeService.activateUserSede(id);
            return ResponseEntity.ok(activatedUserSede);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}