package com.orthodontics.filemanagement.dto;

import com.orthodontics.filemanagement.model.STLFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class STLFileRequest {
    private String fileType;
    private MultipartFile file;
}
