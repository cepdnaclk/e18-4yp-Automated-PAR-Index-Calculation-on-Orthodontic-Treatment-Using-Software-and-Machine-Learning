package com.orthodontics.filemanagement.service;

import com.orthodontics.filemanagement.dto.PatientRegisterRequest;
import com.orthodontics.filemanagement.model.STLFiles;
import com.orthodontics.filemanagement.repository.STLFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

        String prepFileName = stl_id + "_prep_" + patientRegisterRequest.getPrep_file().getOriginalFilename();
        String buccalFileName = stl_id + "_buccal_" + patientRegisterRequest.getBuccal_file().getOriginalFilename();
        String opposingFileName = stl_id + "_opposing_" + patientRegisterRequest.getOpposing_file().getOriginalFilename();

        String prepFilePath = storeFile(patientRegisterRequest.getPrep_file(), prepFileName);
        String buccalFilePath = storeFile(patientRegisterRequest.getBuccal_file(), buccalFileName);
        String opposingFilePath = storeFile(patientRegisterRequest.getOpposing_file(), opposingFileName);

        stlFiles.setPrep(prepFilePath);
        stlFiles.setOpposing(opposingFilePath);
        stlFiles.setBuccal(buccalFilePath);

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
}
