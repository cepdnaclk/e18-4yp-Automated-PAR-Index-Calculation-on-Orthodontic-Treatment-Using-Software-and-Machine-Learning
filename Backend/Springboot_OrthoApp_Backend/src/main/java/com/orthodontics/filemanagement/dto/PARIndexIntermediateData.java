package com.orthodontics.filemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PARIndexIntermediateData {
    private File upper_file;
    private File lower_file;
    private File buccal_file;

    private Object upper_points;
    private Object lower_points;
    private Object buccal_points;
}
