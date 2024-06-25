package com.orthodontics.filemanagement.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientRegisterRequest {
    private String name;
    private String treatment_status;
    private MultipartFile prep_file;
    private MultipartFile opposing_file;
    private MultipartFile buccal_file;
}
