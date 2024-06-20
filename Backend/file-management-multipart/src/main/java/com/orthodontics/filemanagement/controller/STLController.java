package com.orthodontics.filemanagement.controller;

import com.orthodontics.filemanagement.dto.PointResponse;
import com.orthodontics.filemanagement.service.PointService;
import com.orthodontics.filemanagement.service.STLFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.orthodontics.filemanagement.dto.STLResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class STLController {

    private final STLFileService STLFileService;
    private final PointService pointService;

    @GetMapping(value = "/patient/{patientId}/{fileType}")
    public ResponseEntity<Resource> getFiles(@PathVariable Long patientId, @PathVariable String fileType) {

        Resource fileResource = STLFileService.getSTLFile(patientId, fileType);
        List<PointResponse> points = pointService.getPoints(patientId, fileType);

        if (fileResource == null || !fileResource.exists()) {
            return ResponseEntity.notFound().build();
        }

        STLResponse response = STLResponse.builder()
                        .file_type(fileType)
                        .stl_file(fileResource)
                        .points(points)
                        .build();

//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
//                .body(response);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
}
