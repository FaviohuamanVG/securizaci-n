package pe.edu.vallegrande.vg_ms_user.domain.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@JsonPropertyOrder({ "id", "userId", "assignmentReason", "observations", "status", "details" })
@Document(collection = "users_sedes")
public class UserSede {

    @Id
    private String id;
    private String userId;
    private String assignmentReason;
    private String observations;
    private String status;
    private List<UserSedeDetail> details;

    public UserSede() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAssignmentReason() {
        return assignmentReason;
    }

    public void setAssignmentReason(String assignmentReason) {
        this.assignmentReason = assignmentReason;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserSedeDetail> getDetails() {
        return details;
    }

    public void setDetails(List<UserSedeDetail> details) {
        this.details = details;
    }

    public static class UserSedeDetail {
        private String sedeId;
        private Integer sortOrder;
        private String role;
        private String schedule;
        private Date assignedAt;
        private Date activeUntil;
        private List<String> responsibilities;

        public UserSedeDetail() {
        }

        public String getSedeId() {
            return sedeId;
        }

        public void setSedeId(String sedeId) {
            this.sedeId = sedeId;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getSchedule() {
            return schedule;
        }

        public void setSchedule(String schedule) {
            this.schedule = schedule;
        }

        public Date getAssignedAt() {
            return assignedAt;
        }

        public void setAssignedAt(Date assignedAt) {
            this.assignedAt = assignedAt;
        }

        public Date getActiveUntil() {
            return activeUntil;
        }

        public void setActiveUntil(Date activeUntil) {
            this.activeUntil = activeUntil;
        }

        public List<String> getResponsibilities() {
            return responsibilities;
        }

        public void setResponsibilities(List<String> responsibilities) {
            this.responsibilities = responsibilities;
        }
    }
}