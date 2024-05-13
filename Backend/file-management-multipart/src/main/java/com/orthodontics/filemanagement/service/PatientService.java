package com.orthodontics.filemanagement.service;

import com.orthodontics.filemanagement.dto.PatientRegisterRequest;
import com.orthodontics.filemanagement.model.Patient;
import com.orthodontics.filemanagement.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;

    public Long createPatient(PatientRegisterRequest patientRegisterRequest) {
        Patient patient = Patient.builder()
                .name(patientRegisterRequest.getName())
                .treatment_status(patientRegisterRequest.getTreatment_status())
                .build();

        patientRepository.save(patient);
        return patient.getPatient_id();
    }
}
