package com.example.FMIbook.teacher;

import com.example.FMIbook.domain.users.teacher.Teacher;
import com.example.FMIbook.utils.AuthTestUtils;
import com.example.FMIbook.utils.TeacherTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeacherIntegrationTest {
    @Autowired
    private TeacherTestUtils teacherTestUtils;

    @Autowired
    private AuthTestUtils authTestUtils;

    @BeforeAll
    public void addAuthEntities() {
        authTestUtils.addAuthEntities();
    }

    @AfterAll
    public void deleteAuthEntities() {
        authTestUtils.deleteAuthEntities();
    }

    @Test
    public void testAddStudent() throws Exception {
        Teacher teacher = TeacherTestUtils.generateTestTeacher();

        Map<String, Object> response = teacherTestUtils.addOne(
                teacher, authTestUtils.getAdminAccessToken()
        );

        teacherTestUtils.delete(UUID.fromString((String) response.get("id")), authTestUtils.getAdminAccessToken());

        Assert.isTrue(response.get("name").equals(teacher.getName()), "Name is wrong");
        Assert.isTrue(response.get("degree").equals(teacher.getDegree()), "Degree is wrong");
        Assert.isTrue(response.get("email").equals(teacher.getEmail()), "Email is wrong");
        Assert.isTrue(((List<Object>) response.get("courses")).isEmpty(), "Courses are returned");
    }

    @Test
    public void testGetStudentDetails() throws Exception {
        Teacher teacher = TeacherTestUtils.generateTestTeacher();

        Map<String, Object> addedStudent = teacherTestUtils.addOne(teacher, authTestUtils.getAdminAccessToken());

        Map<String, Object> resultStudent = teacherTestUtils.getDetails(
                UUID.fromString((String) addedStudent.get("id")),
                authTestUtils.getAdminAccessToken()
        );
        teacherTestUtils.delete(
                UUID.fromString((String) addedStudent.get("id")),
                authTestUtils.getAdminAccessToken()
        );

        Assert.isTrue(resultStudent.get("name").equals(teacher.getName()), "Name is wrong");
    }

    @Test
    public void testGetNonExistingStudent() throws Exception {
        Map<String, Object> response = teacherTestUtils.getDetails(
                UUID.randomUUID(), authTestUtils.getAdminAccessToken()
        );

        Assert.isTrue(response.containsKey("code"), "Code is empty");
        Assert.isTrue(((Integer) response.get("code")) == 1201, "Code is wrong");
        Assert.isTrue(response.containsKey("status"), "Status is empty");
        Assert.isTrue(((Integer) response.get("status")) == 404, "Status is wrong");
        Assert.isTrue(response.get("message").equals("teacher not found"), "Message is wrong");
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Teacher teacher = TeacherTestUtils.generateTestTeacher();

        Map<String, Object> addedStudent = teacherTestUtils.addOne(
                teacher, authTestUtils.getAdminAccessToken()
        );

        teacherTestUtils.delete(
                UUID.fromString((String) addedStudent.get("id")),
                authTestUtils.getAdminAccessToken()
        );

        Map<String, Object> resultStudent = teacherTestUtils.getDetails(
                UUID.fromString((String) addedStudent.get("id")),
                authTestUtils.getAdminAccessToken()
        );
        Assert.isTrue(resultStudent.containsKey("code"), "Student is found but should not");
    }

    @Test
    public void testUpdateStudent() throws Exception {
        Teacher teacher = TeacherTestUtils.generateTestTeacher();

        Map<String, Object> addedStudent = teacherTestUtils.addOne(
                teacher,
                authTestUtils.getAdminAccessToken());

        teacher.setId(UUID.fromString((String) addedStudent.get("id")));
        teacher.setName("Updated" + teacher.getName());
        teacher.setDegree("Updated" + teacher.getDegree());

        Map<String, Object> updateTeacher = teacherTestUtils.updateOne(
                teacher,
                authTestUtils.getAdminAccessToken());

        teacherTestUtils.delete(UUID.fromString((String) addedStudent.get("id")), authTestUtils.getAdminAccessToken());

        Assert.isTrue(updateTeacher.get("name").equals(teacher.getName()), "Teacher name is not updated");
        Assert.isTrue(updateTeacher.get("degree").equals(teacher.getDegree()), "Teacher degree is not updated");
    }

    @Test
    public void testAddNonValidTeacher() throws Exception {
        Teacher teacher = TeacherTestUtils.generateTestTeacher();
        teacher.setEmail("asdasd");
        teacher.setDegree("");
        teacher.setName("");

        Map<String, Object> errorObject = teacherTestUtils.addOne(teacher, authTestUtils.getAdminAccessToken());
        Assert.isTrue(errorObject.get("status").equals(400), "Status is not valid");
        Assert.isTrue(errorObject.get("message").equals("validation errors"), "Message is not valid");
        Assert.isTrue((
                (Map<String, String>) errorObject.get("errors")).get("name").equals("name is empty"),
                "Name error message is not valid"
        );
        Assert.isTrue((
                        (Map<String, String>) errorObject.get("errors")).get("email").equals("email is invalid"),
                "Email error message is not valid"
        );
        Assert.isTrue((
                        (Map<String, String>) errorObject.get("errors")).get("degree").equals("degree is empty"),
                "Degree error message is not valid"
        );
    }

    @Test
    public void testAddStudentByStudentShouldNotWork() throws Exception {
        Teacher teacher = TeacherTestUtils.generateTestTeacher();

        Map<String, Object> response = teacherTestUtils.addOne(teacher, authTestUtils.getStudentAccessToken());
        Assert.isTrue(response.isEmpty(), "Student is created but should not");
        response = teacherTestUtils.addOne(teacher, authTestUtils.getTeacherAccessToken());
        Assert.isTrue(response.isEmpty(), "Student is created but should not");
    }
}
