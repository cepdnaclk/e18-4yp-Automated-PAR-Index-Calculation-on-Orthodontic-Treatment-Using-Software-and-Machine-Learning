package com.orthodontics.filemanagement.repository;

import com.orthodontics.filemanagement.model.STLFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface STLFileRepository extends JpaRepository<STLFile, Long> {
    long count();
}
