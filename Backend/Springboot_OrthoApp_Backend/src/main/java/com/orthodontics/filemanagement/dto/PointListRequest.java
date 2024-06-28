package com.orthodontics.filemanagement.dto;

import com.orthodontics.filemanagement.model.Point;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointListRequest {
    private Long patient_id;
    private String file_type;
    private String measurement_type;
    private List<Point> points;
}
