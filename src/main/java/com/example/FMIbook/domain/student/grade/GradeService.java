package com.example.FMIbook.domain.student.grade;

import com.example.FMIbook.domain.course.Course;
import com.example.FMIbook.domain.course.CourseDTO;
import com.example.FMIbook.domain.course.CourseRepository;
import com.example.FMIbook.domain.course.exception.CourseNotFoundException;
import com.example.FMIbook.domain.student.Student;
import com.example.FMIbook.domain.student.StudentDTO;
import com.example.FMIbook.domain.student.StudentRepository;
import com.example.FMIbook.domain.student.grade.exception.GradeNotFoundException;
import com.example.FMIbook.server.student.StudentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GradeService {
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public GradeService(GradeRepository gradeRepository, StudentRepository studentRepository, CourseRepository courseRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public List<GradeDTO> findAll(
            Integer limit,
            Integer offset,
            String sort
    ) {
        Sort.Direction orderOptions = Sort.Direction.ASC;
        String sortField = "percentage";
        if (sort != null) {
            sortField = sort.charAt(0) == '-' ? sort.substring(1) : sort;
            orderOptions = sort.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC;
        }
        int pageNumber = offset == null ? 0 : offset;
        int pageSize = limit == null ? 5 : limit;

        Pageable page = PageRequest.of(pageNumber, pageSize,
                orderOptions == Sort.Direction.ASC ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());

        Page<Grade> grades = gradeRepository.findAll(page);
        return grades.getContent().stream().map(GradeDTO::serializeFromEntity).toList();
    }

    public GradeDTO getOne(UUID id) {
        Optional<Grade> grade = gradeRepository.findById(id);
        if (grade.isEmpty()) {
            throw new GradeNotFoundException();
        }

        return GradeDTO.serializeFromEntity(grade.get());
    }

    public GradeDTO addOne(GradeRequestDTO gradeDto) {
        Optional<Student> student = studentRepository.findById(gradeDto.getStudentId());
        Optional<Course> course = courseRepository.findById(gradeDto.getCourseId());

        if (student.isEmpty()) {
            throw new StudentNotFoundException();
        }
        if (course.isEmpty()) {
            throw new CourseNotFoundException();
        }

        Grade grade = new Grade(student.get(), course.get(), gradeDto.getPercentage(), gradeDto.getGrade());
        gradeRepository.save(grade);
        return GradeDTO.serializeFromEntity(grade);
    }

    public GradeDTO update(UUID id, GradeRequestDTO gradeDto) {
        Optional<Grade> gradeOpt = gradeRepository.findById(id);
        if (gradeOpt.isEmpty()) {
            throw new GradeNotFoundException();
        }

        Grade grade = gradeOpt.get();

        if (gradeDto.getPercentage() != null) {
            grade.setPercentage(gradeDto.getPercentage());
        }

        if (gradeDto.getGrade() != null) {
            grade.setGrade(gradeDto.getGrade());
        }

        if (gradeDto.getCourseId() != null) {
            Optional<Course> course = courseRepository.findById(gradeDto.getCourseId());
            if (course.isEmpty()) {
                throw new CourseNotFoundException();
            }
            grade.setCourse(course.get());
        }

        if (gradeDto.getStudentId() != null) {
            Optional<Student> student = studentRepository.findById(gradeDto.getStudentId());
            if (student.isEmpty()) {
                throw new StudentNotFoundException();
            }
            grade.setStudent(student.get());
        }

        gradeRepository.save(grade);
        return GradeDTO.serializeFromEntity(grade);
    }

    public void delete(UUID id) {
        Optional<Grade> grade = gradeRepository.findById(id);
        if (grade.isEmpty()) {
            throw new GradeNotFoundException();
        }
        gradeRepository.delete(grade.get());
    }
}