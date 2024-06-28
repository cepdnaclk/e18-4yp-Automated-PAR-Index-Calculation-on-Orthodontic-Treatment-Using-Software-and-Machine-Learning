package com.orthodontics.filemanagement.service;

import com.orthodontics.filemanagement.dto.*;
import com.orthodontics.filemanagement.model.Patient;
import com.orthodontics.filemanagement.model.Point;
import com.orthodontics.filemanagement.model.STLFiles;
import com.orthodontics.filemanagement.repository.PatientRepository;
import com.orthodontics.filemanagement.repository.PointRepository;
import com.orthodontics.filemanagement.repository.STLFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private STLFileService stlFileService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private STLFileRepository stlFileRepository;

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

    public List<PointsCSVResponse> getAllFilePoints(String fileType) {
        List<Patient> patients = patientRepository.findAll();
        List<PointsCSVResponse> pointsCSVResponses = new ArrayList<>();

        for (Patient patient : patients) {
            Long stlId = stlFileRepository.findByPatient_id(patient.getPatient_id()).getStl_id();
            if (stlId == null) {
                continue;
            }

            List<Point> points = pointRepository.findAllPointsForFile(stlId, fileType);

            List<String> pointNames = points.stream().map(Point::getName).collect(Collectors.toList());
            List<String> pointCoordinates = points.stream()
                    .map(p -> String.format("%s", p.getCoordinates()))
                    .collect(Collectors.toList());

            pointsCSVResponses.add(new PointsCSVResponse(patient.getName(), pointNames, pointCoordinates));
        }
        return pointsCSVResponses;
    }

    public Map<String, List<PARIndexPointsRequest>> getPointsByFileType(long patientID) {
        Long stlId = stlFileService.getSTLFileId(patientID);
        List<Point> points = pointRepository.findAllByStlFiles_id(stlId);

        Map<String, List<PARIndexPointsRequest>> pointsByFileType = new HashMap<>();

        pointsByFileType.put("Upper Arch Segment", points.stream()
                .filter(point -> "Upper Arch Segment".equals(point.getFile_type()))
                .map(point -> new PARIndexPointsRequest(point.getName(), point.getCoordinates()))
                .collect(Collectors.toList()));

        pointsByFileType.put("Lower Arch Segment", points.stream()
                .filter(point -> "Lower Arch Segment".equals(point.getFile_type()))
                .map(point -> new PARIndexPointsRequest(point.getName(), point.getCoordinates()))
                .collect(Collectors.toList()));

        pointsByFileType.put("Buccal Segment", points.stream()
                .filter(point -> "Buccal Segment".equals(point.getFile_type()))
                .map(point -> new PARIndexPointsRequest(point.getName(), point.getCoordinates()))
                .collect(Collectors.toList()));

        return pointsByFileType;
    }
}
