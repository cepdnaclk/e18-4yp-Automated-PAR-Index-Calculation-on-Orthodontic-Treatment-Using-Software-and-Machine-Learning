package com.orthodontics.filemanagement.controller;

import com.orthodontics.filemanagement.dto.PARIndexPointsRequest;
import com.orthodontics.filemanagement.dto.PointListRequest;
import com.orthodontics.filemanagement.dto.PointRequest;
import com.orthodontics.filemanagement.dto.PointsCSVResponse;
import com.orthodontics.filemanagement.service.PointService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointController {

    @Autowired
    private PointService pointService;

    @CrossOrigin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPoint(@RequestBody PointRequest pointRequest) {
        pointService.createPoint(pointRequest);
    }

    @CrossOrigin
    @PostMapping("/list")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPoints(@RequestBody PointListRequest pointListRequest) {
        pointService.createPoints(pointListRequest);
    }

    @CrossOrigin
    @GetMapping
    public Map<String, List<PARIndexPointsRequest>> getPoints(@RequestParam(name = "patient_id") long patientID) {
        return pointService.getPointsByFileType(patientID);
    }

    @CrossOrigin
    @GetMapping("/csv")
    public ResponseEntity<Resource> getFilePointsExcel(@RequestParam(name = "file_type") String fileType) throws IOException {
        List<PointsCSVResponse> pointsCSVResponses = pointService.getAllFilePoints(fileType);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Points");

            // Create a set of all point names for the header
            Set<String> allPointNames = new TreeSet<>();
            for (PointsCSVResponse response : pointsCSVResponses) {
                allPointNames.addAll(response.getPoints());
            }
            List<String> allPointNamesList = new ArrayList<>(allPointNames);

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Patient Name");
            for (int i = 0; i < allPointNamesList.size(); i++) {
                headerRow.createCell(i + 1).setCellValue(allPointNamesList.get(i));
            }

            // Create rows for each patient
            int rowIndex = 1;
            for (PointsCSVResponse response : pointsCSVResponses) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(response.getPatientName());

                Map<String, String> pointMap = new HashMap<>();
                for (int i = 0; i < response.getPoints().size(); i++) {
                    pointMap.put(response.getPoints().get(i), response.getPointCoordinates().get(i));
                }

                for (int i = 0; i < allPointNamesList.size(); i++) {
                    String pointName = allPointNamesList.get(i);
                    String coordinates = pointMap.getOrDefault(pointName, "NULL");
                    row.createCell(i + 1).setCellValue(coordinates);
                }
            }

            workbook.write(byteArrayOutputStream);
            ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"points.xlsx\"")
                    .body(resource);
        }
    }
}
