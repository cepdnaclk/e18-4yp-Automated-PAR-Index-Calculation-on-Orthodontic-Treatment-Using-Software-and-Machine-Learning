package com.orthodontics.filemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointRequest {
    private Long patient_id;
    private String file_type;
    private String measurement_type;
    private String point_name;
    private String coordinates;
}
