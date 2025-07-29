package pe.edu.vallegrande.vg_ms_user.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import pe.edu.vallegrande.vg_ms_user.domain.model.Teacher;


public interface TeacherRepository extends ReactiveMongoRepository<Teacher, String> {
    Flux<Teacher> findByStatus(String status);
    Flux<Teacher> findByUserId(String userId);
}
