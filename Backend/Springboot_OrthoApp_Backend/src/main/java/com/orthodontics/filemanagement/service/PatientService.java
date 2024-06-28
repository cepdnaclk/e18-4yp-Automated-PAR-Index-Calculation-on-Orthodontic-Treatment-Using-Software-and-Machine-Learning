package com.orthodontics.filemanagement.service;

import com.orthodontics.filemanagement.dto.PatientRegisterRequest;
import com.orthodontics.filemanagement.dto.PatientsResponse;
import com.orthodontics.filemanagement.model.Patient;
import com.orthodontics.filemanagement.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public List<PatientsResponse> getAllPatients() {
        List<Patient> allPatients =  patientRepository.findAll();
        List<PatientsResponse> patients = new java.util.ArrayList<>(List.of());

        for (Patient patient : allPatients) {
            PatientsResponse finalPatient= PatientsResponse.builder()
                                                .patient_id(patient.getPatient_id())
                                                .name(patient.getName())
                                                .build();

            if(Objects.equals(patient.getTreatment_status(), "Pre Treatment")) {
                finalPatient.setPre_PAR_score(10.0);
                for (Patient patient1 : allPatients) {
                     if(patient.getName().equals(patient1.getName()) && Objects.equals(patient1.getTreatment_status(), "Post Treatment")) {
                        finalPatient.setPost_PAR_score(20.0);
                        allPatients.remove(patient1);
                    }
                }
            }
            else if(Objects.equals(patient.getTreatment_status(), "Post Treatment")) {
                finalPatient.setPost_PAR_score(20.0);
                for (Patient patient1 : allPatients) {
                    if(patient.getName().equals(patient1.getName()) && Objects.equals(patient1.getTreatment_status(), "Pre Treatment")) {
                        finalPatient.setPre_PAR_score(10.0);
                        allPatients.remove(patient1);
                    }
                }
            }

            patients.add(finalPatient);
        }
        return patients;
    }
}
