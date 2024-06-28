package com.orthodontics.filemanagement.controller;

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

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String processParIndex(@RequestBody Map<String, Object> segments) {
        return parIndexService.processCoordinates(segments);
    }

}
