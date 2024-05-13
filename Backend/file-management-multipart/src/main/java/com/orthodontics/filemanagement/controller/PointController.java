package com.orthodontics.filemanagement.controller;

import com.orthodontics.filemanagement.dto.PointListRequest;
import com.orthodontics.filemanagement.dto.PointRequest;
import com.orthodontics.filemanagement.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPoint(@RequestBody PointRequest pointRequest) {
        pointService.createPoint(pointRequest);
    }

    @PostMapping("/list")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPoints(@RequestBody PointListRequest pointListRequest) {
        pointService.createPoints(pointListRequest);
    }

    @GetMapping
    public List<PointRequest> getPoints(@RequestParam(name = "stl_id") long stl_id) {
        System.out.println(stl_id);
        return null;
    }
}
