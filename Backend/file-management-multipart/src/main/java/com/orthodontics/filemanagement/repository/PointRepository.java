package com.orthodontics.filemanagement.repository;

import com.orthodontics.filemanagement.dto.PointRequest;
import com.orthodontics.filemanagement.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {
    @Query("SELECT p FROM Point p WHERE p.stlFiles_id = ?1")
    List<Point> findAllByStlFiles_id(Long stlId);

    @Query("SELECT p FROM Point p WHERE p.stlFiles_id = ?1 AND p.file_type = ?2")
    List<Point> findAllPointsForFile(Long stlId, String file_type);
}
