package com.orthodontics.filemanagement.dto;

import com.orthodontics.filemanagement.model.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointListRequest {
    private Long stl_id;
    private List<Point> points;
}
