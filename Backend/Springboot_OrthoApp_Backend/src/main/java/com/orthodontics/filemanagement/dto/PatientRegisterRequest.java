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

    // constructor
    public PatientRegisterRequest(MultipartFile prep_file, MultipartFile opposing_file, MultipartFile buccal_file) {
        this.name = null;
        this.treatment_status = null;
        this.prep_file = prep_file;
        this.opposing_file = opposing_file;
        this.buccal_file = buccal_file;
    }
}
