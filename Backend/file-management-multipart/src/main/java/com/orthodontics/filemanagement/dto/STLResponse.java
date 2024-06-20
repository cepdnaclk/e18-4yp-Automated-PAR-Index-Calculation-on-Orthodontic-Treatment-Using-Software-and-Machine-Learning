package com.orthodontics.filemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SLTResponse {
    private String file_type;
    private MultipartFile stl_file;
    private List<PointResponse> points;
}
