package pe.edu.vallegrande.vg_ms_user.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.vg_ms_user.domain.model.Teacher;
import pe.edu.vallegrande.vg_ms_user.infrastructure.repository.TeacherRepository;
import pe.edu.vallegrande.vg_ms_user.infrastructure.repository.UserRepository;

@Service
public class TeacherService {
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    public Mono<Teacher> createTeacher(Teacher teacher) {
        return userRepository.findById(teacher.getUserId())
                .flatMap(user -> {
                    teacher.setStatus(STATUS_ACTIVE);
                    return teacherRepository.save(teacher);
                });
    }

    public Flux<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Flux<Teacher> getActiveTeachers() {
        return teacherRepository.findByStatus(STATUS_ACTIVE);
    }

    public Mono<Teacher> getTeacherById(String id) {
        return teacherRepository.findById(id);
    }

    public Mono<Teacher> updateTeacher(String id, Teacher teacher) {
        return teacherRepository.findById(id)
                .flatMap(existingTeacher -> {
                    teacher.setId(id);
                    if (teacher.getStatus() == null) {
                        teacher.setStatus(existingTeacher.getStatus());
                    }
                    return teacherRepository.save(teacher);
                });
    }

    public Mono<Teacher> deactivateTeacher(String id) {
        return teacherRepository.findById(id)
                .flatMap(teacher -> {
                    teacher.setStatus(STATUS_INACTIVE);
                    return teacherRepository.save(teacher);
                });
    }

    public Mono<Teacher> activateTeacher(String id) {
        return teacherRepository.findById(id)
                .flatMap(teacher -> {
                    teacher.setStatus(STATUS_ACTIVE);
                    return teacherRepository.save(teacher);
                });
    }
} 