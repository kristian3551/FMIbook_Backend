package com.example.FMIbook.domain.course.achievement;

import com.example.FMIbook.domain.course.CourseDTO;
import com.example.FMIbook.domain.users.student.StudentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementDTO {
    private UUID id;
    private String name;
    private String description;
    private StudentDTO student;
    private CourseDTO course;
    private LocalDateTime createdAt;

    public static AchievementDTO serializeLightweight(Achievement achievement, boolean withStudent, boolean withCourse) {
        if (achievement == null) {
            return null;
        }

        return AchievementDTO.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .student(withStudent ? StudentDTO.serializeLightweight(achievement.getStudent()) : null)
                .course(withCourse ? CourseDTO.serializeLightweight(achievement.getCourse()) : null)
                .createdAt(achievement.getCreatedAt())
                .build();
    }

    public static AchievementDTO serializeFromEntity(Achievement achievement) {
        if (achievement == null) {
            return null;
        }

        return AchievementDTO.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .student(StudentDTO.serializeLightweight(achievement.getStudent()))
                .course(CourseDTO.serializeLightweight(achievement.getCourse()))
                .createdAt(achievement.getCreatedAt())
                .build();
    }
}
