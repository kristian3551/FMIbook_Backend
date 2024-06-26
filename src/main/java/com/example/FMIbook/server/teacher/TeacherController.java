package com.example.FMIbook.server.teacher;

import com.example.FMIbook.domain.users.teacher.Teacher;
import com.example.FMIbook.domain.users.teacher.TeacherDTO;
import com.example.FMIbook.domain.users.teacher.TeacherRequestDTO;
import com.example.FMIbook.domain.users.teacher.TeacherService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/teachers")
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public List<TeacherDTO> findAll(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String sort) {
        return teacherService.findAll(limit, offset, sort);
    }

    @GetMapping("{teacherId}")
    public TeacherDTO findOne(@PathVariable UUID teacherId) {
        return teacherService.getOne(teacherId);
    }

    @PostMapping
    public TeacherDTO addOne(@RequestBody @Valid Teacher teacher) {
        return teacherService.addOne(teacher);
    }

    @PutMapping("{teacherId}")
    public TeacherDTO update(@PathVariable UUID teacherId,
                            @RequestBody @Valid TeacherRequestDTO teacherDto) {
        return teacherService.update(teacherId, teacherDto);
    }

    @DeleteMapping("{teacherId}")
    public void delete(@PathVariable UUID teacherId) {
        teacherService.delete(teacherId);
    }
}
