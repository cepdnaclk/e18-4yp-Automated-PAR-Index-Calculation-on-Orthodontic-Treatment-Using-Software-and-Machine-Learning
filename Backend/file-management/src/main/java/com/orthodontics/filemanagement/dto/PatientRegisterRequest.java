package com.orthodontics.filemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientRegisterRequest {
    private String name;
    private String treatment_status;
    private String prep_file;
    private String opposing_file;
    private String buccal_file;
}
