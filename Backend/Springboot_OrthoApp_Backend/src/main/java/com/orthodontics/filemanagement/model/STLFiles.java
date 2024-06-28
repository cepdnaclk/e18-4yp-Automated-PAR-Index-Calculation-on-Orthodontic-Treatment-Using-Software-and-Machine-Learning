package com.orthodontics.filemanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class STLFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stl_id;

    private Long patient_id;
    private String prep;
    private String opposing;
    private String buccal;
}
