package com.orthodontics.filemanagement.controller;

import com.orthodontics.filemanagement.dto.PARIndexIntermediateData;
import com.orthodontics.filemanagement.dto.PARIndexWebRequest;
import com.orthodontics.filemanagement.dto.PatientRegisterRequest;
import com.orthodontics.filemanagement.service.PARIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/parindex")
@RequiredArgsConstructor
public class PARIndexController {

    @Autowired
    private final PARIndexService parIndexService;

    @CrossOrigin
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String processParIndex(@RequestBody Map<String, Object> segments) {
        return parIndexService.processCoordinates(segments);
    }

    @CrossOrigin
    @PostMapping(value = "/predict", consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.OK)
    public String predictParIndex(@ModelAttribute PARIndexWebRequest parIndexWebRequest) {
        PARIndexIntermediateData parIndexIntermediateData = new PARIndexIntermediateData();
        parIndexService.getPredictedPoints(parIndexWebRequest, parIndexIntermediateData);

        String value = parIndexService.processPredictedPoints(parIndexIntermediateData);

        System.out.println(value);
        return value;
    }
}
