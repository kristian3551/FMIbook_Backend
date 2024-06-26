package com.example.FMIbook.domain.course.grade;

import com.example.FMIbook.domain.course.Course;
import com.example.FMIbook.domain.course.task.submission.Submission;
import com.example.FMIbook.domain.users.student.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    @NotNull(message = "percentage is null")
    private Integer percentage;

    @Column
    private Double grade;

    @Column(columnDefinition = "boolean default false")
    private Boolean isFinal;

    @OneToMany(mappedBy = "grade", cascade = CascadeType.ALL)
    private List<Submission> submissions;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Grade{" +
                "uuid=" + id +
                ", student=" + student +
                ", course=" + course +
                ", percentage=" + percentage +
                ", grade=" + grade +
                '}';
    }
}
