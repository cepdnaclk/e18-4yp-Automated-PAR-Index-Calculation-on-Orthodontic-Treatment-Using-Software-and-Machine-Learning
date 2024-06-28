package com.orthodontics.filemanagement.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PatientsResponse {
    private Long patient_id;
    private String name;
    private double pre_PAR_score;
    private double post_PAR_score;
}
