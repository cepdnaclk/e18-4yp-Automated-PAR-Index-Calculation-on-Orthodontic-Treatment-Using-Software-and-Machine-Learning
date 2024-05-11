package com.orthodontics.filemanagement.repository;

import com.orthodontics.filemanagement.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository  extends JpaRepository<Patient, Long> {
}
