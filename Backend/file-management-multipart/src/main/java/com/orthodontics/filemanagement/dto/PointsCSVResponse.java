package com.orthodontics.filemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointsCSVResponse {
    private String patientName;
    private List<String> points;
    private List<String> pointCoordinates;
}
