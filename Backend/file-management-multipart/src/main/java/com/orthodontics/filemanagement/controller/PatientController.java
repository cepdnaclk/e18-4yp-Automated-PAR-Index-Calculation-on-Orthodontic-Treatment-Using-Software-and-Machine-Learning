package com.orthodontics.filemanagement.controller;

import com.orthodontics.filemanagement.dto.PatientRegisterRequest;
import com.orthodontics.filemanagement.dto.PatientRegisterResponse;
import com.orthodontics.filemanagement.dto.PointResponse;
import com.orthodontics.filemanagement.service.PatientService;
import com.orthodontics.filemanagement.service.STLFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final STLFileService STLFileService;

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<PatientRegisterResponse> registerPatient(@ModelAttribute PatientRegisterRequest patientRegisterRequest) throws IOException {
        Long patient_id = patientService.createPatient(patientRegisterRequest);

        STLFileService.createSTLFile(patientRegisterRequest, patient_id);

        PatientRegisterResponse response = PatientRegisterResponse.builder()
                .patient_id(patient_id)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
