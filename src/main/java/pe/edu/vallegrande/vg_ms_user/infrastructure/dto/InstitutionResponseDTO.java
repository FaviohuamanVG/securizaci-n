package pe.edu.vallegrande.vg_ms_user.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionResponseDTO {
    private String id;
    private String institutionName;
    private String codeName;
    private String institutionLogo;
    private String modularCode;
    private String address;
    private String contactEmail;
    private String contactPhone;
    private String status;
    private Map<String, Object> uiSettings;
    private Map<String, Object> evaluationSystem;
    private Map<String, Object> scheduleSettings;
    private Date createdAt;
    private List<String> headquarterIds;
}
