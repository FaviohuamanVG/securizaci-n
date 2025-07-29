package pe.edu.vallegrande.vg_ms_user.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeadquarterResponseDTO {
    private String id;
    private String institutionId;
    private String headquartersName;
    private String headquartersCode;
    private String address;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String status;
}