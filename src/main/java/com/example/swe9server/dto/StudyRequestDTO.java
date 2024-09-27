package com.example.swe9server.dto;

import com.example.swe9server.entity.enumeration.Campus;
import com.example.swe9server.entity.enumeration.Day;
import com.example.swe9server.entity.enumeration.Level;
import com.example.swe9server.entity.enumeration.Semester;
import lombok.Data;

import java.util.List;

@Data
public class StudyRequestDTO {
    private Long id;
    private String title;
    private String imageSrc;
    private Day day;
    private String startTime;
    private String endTime;
    private Level level;
    private List<String> stacks;
    private Campus campus;
    private String description;
    private Boolean isRecruiting;
    private Semester semester;
}
