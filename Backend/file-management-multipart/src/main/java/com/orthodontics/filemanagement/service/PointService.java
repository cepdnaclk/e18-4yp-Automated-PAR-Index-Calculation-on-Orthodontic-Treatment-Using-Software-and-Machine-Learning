package com.orthodontics.filemanagement.service;

import com.orthodontics.filemanagement.dto.PointListRequest;
import com.orthodontics.filemanagement.dto.PointRequest;
import com.orthodontics.filemanagement.dto.PointResponse;
import com.orthodontics.filemanagement.model.Point;
import com.orthodontics.filemanagement.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private STLFileService stlFileService;

    public void createPoint(PointRequest pointRequest) {
        Long stl_id = stlFileService.getSTLFileId(pointRequest.getPatient_id());
        Point point = Point.builder()
                .stlFiles_id(stl_id)
                .file_type(pointRequest.getFile_type())
                .point_name(pointRequest.getPoint_name())
                .coordinates(pointRequest.getCoordinates())
                .build();

        pointRepository.save(point);
    }

    public void createPoints(PointListRequest pointListRequest) {
        Long stl_id = stlFileService.getSTLFileId(pointListRequest.getPatient_id());
        String file_type = pointListRequest.getFile_type();
        String measurement_type = pointListRequest.getMeasurement_type();

        List<Point> points = pointListRequest.getPoints();

        for (Point point : points) {
            point.setStlFiles_id(stl_id);
            point.setFile_type(file_type);
            pointRepository.save(point);
        }
    }

    public List<PointResponse> getPoints(Long patient_id, String file_type) {
        Long stl_id = stlFileService.getSTLFileId(patient_id);
        List<Point> allPoints = pointRepository.findAllPointsForFile(stl_id, file_type);
        List<PointResponse> points = new java.util.ArrayList<>(List.of());

        for (Point point : allPoints) {
            PointResponse finalPoint = PointResponse.builder()
                    .point_name(point.getPoint_name())
                    .coordinates(point.getCoordinates())
                    .build();
            points.add(finalPoint);
        }
        return points;
    }
}
