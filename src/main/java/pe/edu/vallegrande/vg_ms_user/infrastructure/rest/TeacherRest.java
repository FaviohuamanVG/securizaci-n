package pe.edu.vallegrande.vg_ms_user.infrastructure.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;import pe.edu.vallegrande.vg_ms_user.domain.model.Teacher;
import pe.edu.vallegrande.vg_ms_user.application.service.TeacherService;

@RestController
@RequestMapping("/api/v1/teachers")
@CrossOrigin(origins = "*")
public class TeacherRest {
    @Autowired
    private TeacherService teacherService;

    @PostMapping
    public Mono<Teacher> createTeacher(@RequestBody Teacher teacher) {
        return teacherService.createTeacher(teacher);
    }

    @GetMapping
    public Flux<Teacher> getAllTeachers(@RequestParam(required = false) String status) {
        if ("active".equalsIgnoreCase(status)) {
            return teacherService.getActiveTeachers();
        }
        return teacherService.getAllTeachers();
    }

    @GetMapping("/{id}")
    public Mono<Teacher> getTeacherById(@PathVariable String id) {
        return teacherService.getTeacherById(id);
    }

    @PutMapping("/{id}")
    public Mono<Teacher> updateTeacher(@PathVariable String id, @RequestBody Teacher teacher) {
        return teacherService.updateTeacher(id, teacher);
    }

    @PatchMapping("/{id}/deactivate")
    public Mono<Teacher> deactivateTeacher(@PathVariable String id) {
        return teacherService.deactivateTeacher(id);
    }

    @PatchMapping("/{id}/activate")
    public Mono<Teacher> activateTeacher(@PathVariable String id) {
        return teacherService.activateTeacher(id);
    }
}