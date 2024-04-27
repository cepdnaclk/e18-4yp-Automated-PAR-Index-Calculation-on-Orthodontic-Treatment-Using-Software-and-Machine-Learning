package com.orthodontics.filemanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class STLFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stl_id;

    private String filename;
    private String location;
    private String fileType;
}
