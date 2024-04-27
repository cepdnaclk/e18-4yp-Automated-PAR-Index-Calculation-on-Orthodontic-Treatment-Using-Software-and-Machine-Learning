package com.orthodontics.filemanagement.repository;

import com.orthodontics.filemanagement.dto.PointRequest;
import com.orthodontics.filemanagement.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {
//    @Query(value = "SELECT item_id,quantity FROM item_quantity WHERE order_id = :id", nativeQuery = true)
//    List<>
}
