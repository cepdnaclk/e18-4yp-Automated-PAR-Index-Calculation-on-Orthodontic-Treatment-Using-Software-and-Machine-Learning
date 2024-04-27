package com.orthodontics.filemanagement.controller;

import com.orthodontics.filemanagement.dto.STLFileRequest;
import com.orthodontics.filemanagement.dto.STLFileResponse;
import com.orthodontics.filemanagement.model.STLFile;
import com.orthodontics.filemanagement.service.STLFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/api/stlfile")
@RestController
public class STLFileController {

    @Autowired
    STLFileService stlFileService;

    @PostMapping(consumes = {"multipart/form-data"})
    public STLFileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("fileType") String fileType) throws IOException {
        STLFileRequest stlFileRequest = new STLFileRequest();
        stlFileRequest.setFile(file);
        stlFileRequest.setFileType(fileType);

        STLFile stlFile = stlFileService.uploadSTLFile(stlFileRequest);
        return new STLFileResponse(stlFile.getStl_id());
    }
}
