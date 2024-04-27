package com.orthodontics.filemanagement.service;

import com.orthodontics.filemanagement.dto.PointListRequest;
import com.orthodontics.filemanagement.dto.PointRequest;
import com.orthodontics.filemanagement.model.Point;
import com.orthodontics.filemanagement.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

    private final PointRepository pointRepository;

    public void createPoint(PointRequest pointRequest) {
        Point point = Point.builder()
                .stl_id(pointRequest.getStl_id())
                .point_name(pointRequest.getPoint_name())
                .coordinates(pointRequest.getCoordinates())
                .build();


        pointRepository.save(point);
        log.info("Point created: {}", point);
    }

    public void createPoints(PointListRequest pointListRequest) {
        Long stl_id = pointListRequest.getStl_id();
        List<Point> points = pointListRequest.getPoints();

        for (Point point : points) {
            point.setStl_id(stl_id);
            pointRepository.save(point);
        }
    }
}
