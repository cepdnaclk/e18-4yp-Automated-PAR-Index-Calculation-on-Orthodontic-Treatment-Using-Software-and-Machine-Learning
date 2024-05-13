package com.orthodontics.filemanagement.repository;

import com.orthodontics.filemanagement.dto.PointRequest;
import com.orthodontics.filemanagement.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {
}
