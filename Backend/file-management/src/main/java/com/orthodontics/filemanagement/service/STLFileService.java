package com.orthodontics.filemanagement.service;

import com.orthodontics.filemanagement.dto.PatientRegisterRequest;
import com.orthodontics.filemanagement.model.STLFiles;
import com.orthodontics.filemanagement.repository.STLFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class STLFileService {

    private final STLFileRepository STLFileRepository;

    public void createSTLFile(PatientRegisterRequest patientRegisterRequest, Long patient_id) {
        STLFiles stlFiles = STLFiles.builder()
                .patient_id(patient_id)
                .prep(patientRegisterRequest.getPrep_file())
                .buccal(patientRegisterRequest.getBuccal_file())
                .opposing(patientRegisterRequest.getOpposing_file())
                .build();

        STLFileRepository.save(stlFiles);
        log.info("STLFile created: {}", stlFiles);
    }

    public Long getSTLFileId(Long patient_id) {
        return STLFileRepository.findByPatient_id(patient_id).getStl_id();
    }
}
