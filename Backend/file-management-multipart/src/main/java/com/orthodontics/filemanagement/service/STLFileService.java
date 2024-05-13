package com.orthodontics.filemanagement.service;

import com.orthodontics.filemanagement.dto.PatientRegisterRequest;
import com.orthodontics.filemanagement.model.STLFiles;
import com.orthodontics.filemanagement.repository.STLFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class STLFileService {

    private final STLFileRepository STLFileRepository;
    private static final String UPLOAD_DIR = "C:/Users/META/OneDrive/Documents/STLData";

    public void createSTLFile(PatientRegisterRequest patientRegisterRequest, Long patient_id) throws IOException {
        STLFiles stlFiles = STLFiles.builder()
                .patient_id(patient_id)
                .prep(storeFile(patientRegisterRequest.getPrep_file()))
                .buccal(storeFile(patientRegisterRequest.getBuccal_file()))
                .opposing(storeFile(patientRegisterRequest.getOpposing_file()))
                .build();

        STLFileRepository.save(stlFiles);
        log.info("STLFile created: {}", stlFiles);
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            Path targetLocation = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
            Files.copy(file.getInputStream(), targetLocation);
            return targetLocation.toAbsolutePath().toString();
        }
        return null;
    }

    public Long getSTLFileId(Long patient_id) {
        return STLFileRepository.findByPatient_id(patient_id).getStl_id();
    }
}
