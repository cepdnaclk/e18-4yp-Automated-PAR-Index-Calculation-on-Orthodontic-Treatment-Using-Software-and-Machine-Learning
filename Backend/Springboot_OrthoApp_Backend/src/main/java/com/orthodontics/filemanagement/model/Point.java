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
@Getter
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long point_ID;

    private Long stlFiles_id;
    private String file_type;
    private String point_name;
    private String coordinates;

    public String getName() {
        return point_name;
    }
}
