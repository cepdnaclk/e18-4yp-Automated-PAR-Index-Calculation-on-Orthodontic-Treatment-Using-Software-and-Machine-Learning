package com.orthodontics.filemanagement.repository;

import com.orthodontics.filemanagement.model.STLFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface STLFileRepository extends JpaRepository<STLFiles, Long> {
    @Query("SELECT s FROM STLFiles s WHERE s.patient_id = ?1")
    STLFiles findByPatient_id(Long patientId);
}
