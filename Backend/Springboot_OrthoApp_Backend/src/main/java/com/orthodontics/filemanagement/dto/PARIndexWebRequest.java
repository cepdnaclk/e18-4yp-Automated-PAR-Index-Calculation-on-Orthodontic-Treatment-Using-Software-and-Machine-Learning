package com.orthodontics.filemanagement.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PARIndexWebRequest {
    private MultipartFile upper_stl;
    private MultipartFile lower_stl;
    private MultipartFile buccal_stl;
}
