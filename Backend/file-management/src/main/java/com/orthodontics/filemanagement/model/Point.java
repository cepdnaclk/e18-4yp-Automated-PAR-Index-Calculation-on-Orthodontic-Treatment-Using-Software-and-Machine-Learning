package com.orthodontics.filemanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long point_ID;

    private Long stl_id;
    private String point_name;
    private String coordinates;
}
