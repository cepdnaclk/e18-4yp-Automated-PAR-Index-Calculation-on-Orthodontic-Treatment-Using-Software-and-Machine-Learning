package com.orthodontics.filemanagement.service;

import com.orthodontics.filemanagement.dto.STLFileRequest;
import com.orthodontics.filemanagement.model.STLFile;
import com.orthodontics.filemanagement.repository.STLFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class STLFileService {

    private final STLFileRepository stlFileRepository;

    @Value("${app.fileLocation}")
    private String FOLDER_PATH;

    public STLFile uploadSTLFile(STLFileRequest stlFileRequest) throws IOException {
        String originalFilename = stlFileRequest.getFile().getOriginalFilename();

        STLFile stlFile = STLFile.builder()
                .filename(originalFilename)
                .fileType(stlFileRequest.getFileType())
                .build();

        // Save the file to the database to get the generated stl_id
        stlFile = stlFileRepository.save(stlFile);

        // Rename the file with stl_id and filename
        String newFilename = this.getNextStlId() + "_" + originalFilename;
        String filePath = FOLDER_PATH + newFilename;

        // Update the file location in the database
        stlFile.setLocation(filePath);
        stlFileRepository.save(stlFile);

        // Transfer the file to the new location
        stlFileRequest.getFile().transferTo(new File(filePath));

        return stlFile;
    }

    public byte[] downloadFile(Long fileId) throws IOException {
        Optional<STLFile> stlFile = stlFileRepository.findById(fileId);
        if(stlFile.isPresent()) {
            String filePath = stlFile.get().getLocation();
            return Files.readAllBytes(new File(filePath).toPath());
        }
        return null;
    }

    public long getNextStlId() {
        return stlFileRepository.count() + 1;
    }
}
