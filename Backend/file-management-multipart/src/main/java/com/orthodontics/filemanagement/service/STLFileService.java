package com.orthodontics.filemanagement.service;

import com.orthodontics.filemanagement.dto.PatientRegisterRequest;
import com.orthodontics.filemanagement.model.STLFiles;
import com.orthodontics.filemanagement.repository.STLFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class STLFileService {

    private final STLFileRepository STLFileRepository;

    @Value("${app.fileLocation}")
    private String FOLDER_PATH;

    public void createSTLFile(PatientRegisterRequest patientRegisterRequest, Long patient_id) throws IOException {

        STLFiles stlFiles = STLFiles.builder()
                .patient_id(patient_id)
                .build();

        STLFileRepository.save(stlFiles);
        Long stl_id = stlFiles.getStl_id();
        if (patientRegisterRequest.getPrep_file() != null) {
            String prepFileName = stl_id + "_prep_" + patientRegisterRequest.getPrep_file().getOriginalFilename();
            String prepFilePath = storeFile(patientRegisterRequest.getPrep_file(), prepFileName);
            stlFiles.setPrep(prepFilePath);
        }

        if (patientRegisterRequest.getBuccal_file() != null) {
            String buccalFileName = stl_id + "_buccal_" + patientRegisterRequest.getBuccal_file().getOriginalFilename();
            String buccalFilePath = storeFile(patientRegisterRequest.getBuccal_file(), buccalFileName);
            stlFiles.setBuccal(buccalFilePath);
        }

        if (patientRegisterRequest.getOpposing_file() != null) {
            String opposingFileName = stl_id + "_opposing_" + patientRegisterRequest.getOpposing_file().getOriginalFilename();
            String opposingFilePath = storeFile(patientRegisterRequest.getOpposing_file(), opposingFileName);
            stlFiles.setOpposing(opposingFilePath);
        }

        STLFileRepository.save(stlFiles);

        log.info("STLFile created: {}", stlFiles);
    }

    private String storeFile(MultipartFile file, String fileName) throws IOException {
        if (file != null && !file.isEmpty()) {
            String filePath = FOLDER_PATH + fileName;
            file.transferTo(new File(filePath));
            return filePath;
        }
        return null;
    }

    public Long getSTLFileId(Long patient_id) {
        return STLFileRepository.findByPatient_id(patient_id).getStl_id();
    }

    public Resource getSTLFile(Long patient_id, String file_Type) {
        Long stl_id = getSTLFileId(patient_id);
        STLFiles stlFiles = STLFileRepository.findByPatient_id(stl_id);

        String filePath = switch (file_Type) {
            case "Lower" -> stlFiles.getPrep();
            case "buccal" -> stlFiles.getBuccal();
            case "Upper" -> stlFiles.getOpposing();
            default -> null;
        };

        if (filePath != null) {
            File file = new File(filePath);
            System.out.println(filePath);
            return new FileSystemResource(file);
        }

        return null;
    }
}
